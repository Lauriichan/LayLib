package me.lauriichan.laylib.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;

public final class JavaAccess {

    private static class FieldRecord {

        private final Field field;
        private final JavaLookup lookup;

        private volatile MethodHandle getter, setter;

        public FieldRecord(JavaLookup lookup, Field field) {
            this.lookup = lookup;
            this.field = field;
        }

        public MethodHandle getter() {
            if (getter != null) {
                return getter;
            }
            return getter = lookup.unreflectGetter(field);
        }

        public MethodHandle setter() {
            if (setter != null) {
                return setter;
            }
            return setter = lookup.unreflectSetter(field);
        }
    }

    public static final JavaAccess PLATFORM = new JavaAccess(JavaLookup.PLATFORM);

    private final Object2ObjectMap<Constructor<?>, MethodHandle> constructors = Object2ObjectMaps
        .synchronize(new Object2ObjectArrayMap<>());
    private final Object2ObjectMap<Method, MethodHandle> methods = Object2ObjectMaps.synchronize(new Object2ObjectArrayMap<>());
    private final Object2ObjectMap<Field, FieldRecord> fields = Object2ObjectMaps.synchronize(new Object2ObjectArrayMap<>());

    private final JavaLookup lookup;

    private volatile boolean allowUnsafeAccess;

    public JavaAccess(JavaLookup lookup) {
        if (PLATFORM != null && lookup == JavaLookup.PLATFORM) {
            throw new UnsupportedOperationException("Only one platform instance allowed");
        }
        this.lookup = lookup;
        this.allowUnsafeAccess = lookup == JavaLookup.PLATFORM;
    }

    public final boolean allowUnsafeAccess() {
        return allowUnsafeAccess;
    }

    public final JavaAccess allowUnsafeAccess(boolean allowUnsafeAccess) {
        if (lookup == JavaLookup.PLATFORM) {
            return this;
        }
        this.allowUnsafeAccess = allowUnsafeAccess;
        return this;
    }

    public final JavaLookup lookup() {
        return lookup;
    }

    public final void clear() {
        fields.clear();
    }

    public <E> E getValue(Field field) throws AccessFailedException {
        return getValue(field, null);
    }

    public <E> E getValue(Field field, Object instance) throws AccessFailedException {
        boolean isStatic;
        if (!(isStatic = Modifier.isStatic(field.getModifiers())) && instance == null) {
            throw new IllegalArgumentException("Can't get value of non-static field without object instance");
        }
        FieldRecord rec = fields.get(field);
        if (rec == null) {
            rec = new FieldRecord(lookup, field);
            fields.put(field, rec);
        }
        try {
            if (isStatic) {
                return (E) rec.getter().invoke(instance);
            } else {
                return (E) rec.getter().invoke();
            }
        } catch (Throwable e) {
            if (allowUnsafeAccess) {
                return JavaUnsafe.UNSAFE.getValue(field, instance);
            }
            throw new AccessFailedException("Failed to access field: " + field.getName(), e);
        }
    }

    public void setValue(Field field, Object value) {
        setValue(field, null, value);
    }

    public void setValue(Field field, Object instance, Object value) {
        boolean isStatic;
        if (!(isStatic = Modifier.isStatic(field.getModifiers())) && instance == null) {
            throw new IllegalArgumentException("Can't set value of non-static field without object instance");
        }
        FieldRecord rec = fields.get(field);
        if (rec == null) {
            rec = new FieldRecord(lookup, field);
            fields.put(field, rec);
        }
        try {
            if (isStatic) {
                rec.setter().invoke(instance, value);
                return;
            } else {
                rec.setter().invoke(value);
                return;
            }
        } catch (Throwable e) {
            if (allowUnsafeAccess) {
                JavaUnsafe.UNSAFE.setValue(field, instance, value);
                return;
            }
            throw new AccessFailedException("Failed to access field: " + field.getName(), e);
        }
    }

    public <E> E invoke(Method method, Object... arguments) {
        return invoke(null, method, arguments);
    }

    @SuppressWarnings("unchecked")
    public <E> E invoke(Object instance, Method method, Object... arguments) {
        boolean isStatic;
        if (!(isStatic = Modifier.isStatic(method.getModifiers())) && instance == null) {
            throw new IllegalArgumentException("Can't invoke non-static method without object instance");
        }
        MethodHandle handle = methods.get(method);
        if (handle == null) {
            handle = lookup.unreflect(method);
            methods.put(method, handle);
        }
        try {
            if (isStatic) {
                return (E) handle.invokeWithArguments(arguments);
            }
            Object[] newArgs = new Object[arguments.length + 1];
            newArgs[0] = instance;
            System.arraycopy(arguments, 0, newArgs, 1, arguments.length);
            return (E) handle.invokeWithArguments(newArgs);
        } catch (Throwable e) {
            throw new AccessFailedException("Failed to access method: " + method.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> E invoke(Constructor<?> constructor, Object... arguments) {
        MethodHandle handle = constructors.get(constructor);
        if (handle == null) {
            handle = lookup.unreflect(constructor);
            constructors.put(constructor, handle);
        }
        try {
            return (E) handle.invokeWithArguments(arguments);
        } catch (Throwable e) {
            throw new AccessFailedException("Failed to access constructor of class: " + constructor.getDeclaringClass().getName(), e);
        }
    }
    
    public <E> E instance(Class<E> type) {
        try {
            return invoke(type.getDeclaredConstructor());
        } catch (NoSuchMethodException | SecurityException e) {
            throw new AccessFailedException("Failed to access constructor of class: " + type.getName(), e);
        }
    }

}