package me.lauriichan.laylib.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import sun.misc.Unsafe;

public final class JavaUnsafe {

    public static final JavaUnsafe UNSAFE = new JavaUnsafe();

    private final Unsafe unsafe;

    private volatile MethodHandle modifierSetter;

    private JavaUnsafe() {
        if (UNSAFE != null) {
            throw new UnsupportedOperationException("Only one instance allowed");
        }
        this.unsafe = acquireUnsafe();
    }

    private final MethodHandle modifierSetter() {
        if (modifierSetter != null) {
            return modifierSetter;
        }
        return modifierSetter = acquireModifierSetter();
    }

    private final MethodHandle acquireModifierSetter() {
        try {
            return JavaLookup.PLATFORM.lookup().findSetter(Field.class, "modifiers", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private final Unsafe acquireUnsafe() {
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (final Exception exp) {
            return null;
        }
    }

    public <E> E getValue(final Field field) {
        return getValue(field, null);
    }

    @SuppressWarnings("unchecked")
    public <E> E getValue(final Field field, final Object instance) {
        boolean isStatic;
        if (!(isStatic = Modifier.isStatic(field.getModifiers())) && instance == null) {
            throw new IllegalArgumentException("Can't get value of non-static field without object instance");
        }
        Object base = isStatic ? unsafe.staticFieldBase(field) : instance;
        long offset = isStatic ? unsafe.staticFieldOffset(field) : unsafe.objectFieldOffset(field);
        if (Modifier.isVolatile(field.getModifiers())) {
            return (E) unsafe.getObjectVolatile(base, offset);
        }
        return (E) unsafe.getObject(base, offset);
    }

    public void setValue(final Field field, final Object value) {
        setValue(field, null, value);
    }

    public void setValue(final Field field, final Object instance, Object value) {
        boolean isStatic;
        if (!(isStatic = Modifier.isStatic(field.getModifiers())) && instance == null) {
            throw new IllegalArgumentException("Can't set value of non-static field without object instance");
        }
        unfinalize(field);
        Object base = isStatic ? unsafe.staticFieldBase(field) : instance;
        long offset = isStatic ? unsafe.staticFieldOffset(field) : unsafe.objectFieldOffset(field);
        value = (value != null ? (field.getType().isPrimitive() ? value : field.getType().cast(value)) : null);
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putObjectVolatile(base, offset, value);
            return;
        }
        unsafe.putObject(base, offset, value);
    }

    void unfinalize(final Field field) {
        if (!Modifier.isFinal(field.getModifiers())) {
            return;
        }
        try {
            modifierSetter().invokeExact(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (Throwable e) {
            // Ignore this
        }
    }

}
