package me.lauriichan.laylib.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

import sun.misc.Unsafe;

public final class JavaAccess {

    private static final JavaAccess INSTANCE = new JavaAccess();

    private Unsafe unsafe;
    private Lookup lookup;

    private JavaAccess() {
        final Optional<Class<?>> option = StackTracker.getCallerClass();
        if (!option.isPresent() || option.get() != JavaAccess.class) {
            throw new UnsupportedOperationException("Utility class");
        }
    }

    public Unsafe unsafe() {
        if (unsafe != null) {
            return unsafe;
        }
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return unsafe = (Unsafe) field.get(null);
        } catch (final Exception exp) {
            return null;
        }
    }

    public Lookup lookup() {
        if (lookup != null) {
            return lookup;
        }
        return lookup = (Lookup) getStaticValueUnsafe(ClassUtil.getField(Lookup.class, "IMPL_LOOKUP"));
    }

    /*
     * Method invokation
     */

    public Object execute(final Object instance, final Method method, final Object... arguments) {
        if (method == null || method.getParameterCount() != arguments.length) {
            return null;
        }
        try {
            return executeThrows(instance, method, arguments);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object executeThrows(final Object instance, final Method method, final Object... arguments) throws Throwable {
        if (method == null || method.getParameterCount() != arguments.length) {
            return null;
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            if (instance == null) {
                return null;
            }
            if (arguments.length == 0) {
                return lookup().unreflect(method).invokeWithArguments(instance);
            }
            final Object[] input = new Object[arguments.length + 1];
            input[0] = instance;
            System.arraycopy(arguments, 0, input, 1, arguments.length);
            return lookup().unreflect(method).invokeWithArguments(input);
        }
        return lookup().unreflect(method).invokeWithArguments(arguments);
    }

    public Object init(final Constructor<?> constructor, final Object... arguments) {
        if (constructor == null || constructor.getParameterCount() != arguments.length) {
            return null;
        }
        try {
            return lookup().unreflectConstructor(constructor).invokeWithArguments(arguments);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object initThrows(final Constructor<?> constructor, final Object... arguments) throws Throwable {
        if (constructor == null || constructor.getParameterCount() != arguments.length) {
            return null;
        }
        return lookup().unreflectConstructor(constructor).invokeWithArguments(arguments);
    }

    /*
     * Safe Accessors
     */

    public VarHandle handle(final Field field, final boolean force) {
        if (field == null) {
            return null;
        }
        if (force) {
            unfinalize(field);
        }
        try {
            return lookup().unreflectVarHandle(field);
        } catch (final Throwable e) {
            return null;
        }
    }

    public MethodHandle handleGetter(final Field field) {
        if (field == null) {
            return null;
        }
        try {
            return lookup().unreflectGetter(field);
        } catch (final Throwable e) {
            return null;
        }
    }

    public MethodHandle handleSetter(final Field field) {
        if (field == null) {
            return null;
        }
        unfinalize(field);
        try {
            return lookup().unreflectSetter(field);
        } catch (final Throwable e) {
            return null;
        }
    }

    public MethodHandle handle(final Method method) {
        if (method == null) {
            return null;
        }
        try {
            return lookup().unreflect(method);
        } catch (final Throwable e) {
            return null;
        }
    }

    public MethodHandle handle(final Constructor<?> constructor) {
        if (constructor == null) {
            return null;
        }
        try {
            return lookup().unreflectConstructor(constructor);
        } catch (final Throwable e) {
            return null;
        }
    }

    /*
     * Safe Accessors helper
     */

    public Object executeSafe(final Object instance, final MethodHandle handle, final Object... arguments) {
        if (handle == null || handle.type().parameterCount() != arguments.length) {
            return null;
        }
        try {
            if (instance != null) {
                if (arguments.length == 0) {
                    return handle.invokeWithArguments(instance);
                }
                final Object[] input = new Object[arguments.length + 1];
                input[0] = instance;
                System.arraycopy(arguments, 0, input, 1, arguments.length);
                return handle.invokeWithArguments(input);
            }
            return handle.invokeWithArguments(arguments);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object getValueSafe(final Object instance, final VarHandle handle) {
        if (handle == null) {
            return null;
        }
        try {
            if (instance == null) {
                return handle.getVolatile();
            }
            return handle.getVolatile(instance);
        } catch (final Throwable e) {
            throw new AccessUnsuccessful();
        }
    }

    public void setValueSafe(final Object instance, final VarHandle handle, final Object value) {
        if (handle == null || value != null && !handle.varType().isAssignableFrom(value.getClass())) {
            return;
        }
        try {
            if (instance != null) {
                handle.setVolatile(value);
                return;
            }
            handle.setVolatile(instance, value);
        } catch (final Throwable e) {
            throw new AccessUnsuccessful();
        }
    }

    /*
     * Safe Field Modifier
     */

    public Object getObjectValueSafe(final Object instance, final Field field) {
        if (instance == null || field == null) {
            return null;
        }
        try {
            return lookup().unreflectVarHandle(field).get(instance);
        } catch (final Throwable e) {
            throw new AccessUnsuccessful();
        }
    }

    public Object getStaticValueSafe(final Field field) {
        if (field == null) {
            return null;
        }
        try {
            return lookup().unreflectVarHandle(field).get();
        } catch (final Throwable e) {
            throw new AccessUnsuccessful();
        }
    }

    public void setObjectValueSafe(final Object instance, final Field field, final Object value) {
        if (instance == null || field == null) {
            return;
        }
        unfinalize(field);
        try {
            lookup().unreflectVarHandle(field).set(instance, value);
        } catch (final Throwable e) {
            throw new AccessUnsuccessful();
        }
    }

    public void setStaticValueSafe(final Field field, final Object value) {
        if (field == null) {
            return;
        }
        unfinalize(field);
        try {
            lookup().unreflectVarHandle(field).set(value);
        } catch (final Throwable e) {
            throw new AccessUnsuccessful();
        }
    }

    /*
     * Unsafe Field Modifier
     */

    public Object getObjectValueUnsafe(final Object instance, final Field field) {
        if (instance == null || field == null) {
            return null;
        }
        final Unsafe unsafe = unsafe();
        return unsafe.getObjectVolatile(instance, unsafe.objectFieldOffset(field));
    }

    public Object getStaticValueUnsafe(final Field field) {
        if (field == null) {
            return null;
        }
        final Unsafe unsafe = unsafe();
        return unsafe.getObjectVolatile(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
    }

    public void setObjectValueUnsafe(final Object instance, final Field field, final Object value) {
        if (instance == null || field == null) {
            return;
        }
        unfinalize(field);
        final Unsafe unsafe = unsafe();
        if (value == null) {
            unsafe.putObject(instance, unsafe.objectFieldOffset(field), null);
            return;
        }
        unsafe.putObject(instance, unsafe.objectFieldOffset(field), field.getType().cast(value));
    }

    public void setStaticValueUnsafe(final Field field, final Object value) {
        if (field == null) {
            return;
        }
        unfinalize(field);
        final Unsafe unsafe = unsafe();
        if (value == null) {
            unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), null);
            return;
        }
        unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), field.getType().cast(value));
    }

    /*
     * Internal Utilities
     */

    private void unfinalize(final Field field) {
        if (!Modifier.isFinal(field.getModifiers())) {
            return;
        }
        try {
            lookup().findSetter(Field.class, "modifiers", int.class).invokeExact(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (final Throwable e) {
            // Ignore
        }
    }

    /*
     * Static Accessors Helper
     */

    public static Object getStaticValue(final VarHandle handle) {
        return INSTANCE.getValueSafe(null, handle);
    }

    public static Object getValue(final Object instance, final VarHandle handle) {
        return INSTANCE.getValueSafe(instance, handle);
    }

    public static void setStaticValue(final VarHandle handle, final Object value) {
        INSTANCE.setValueSafe(null, handle, value);
    }

    public static void setValue(final Object instance, final VarHandle handle, final Object value) {
        INSTANCE.setValueSafe(instance, handle, value);
    }

    public static Object invokeStatic(final MethodHandle handle, final Object... arguments) {
        return INSTANCE.executeSafe(null, handle, arguments);
    }

    public static Object invoke(final Object instance, final MethodHandle handle, final Object... arguments) {
        return INSTANCE.executeSafe(instance, handle, arguments);
    }

    /*
     * Static Implementation
     */

    // Invokation

    public static Object instance(final Class<?> clazz) {
        return INSTANCE.init(ClassUtil.getConstructor(clazz));
    }

    public static Object instance(final Constructor<?> constructor, final Object... arguments) {
        return INSTANCE.init(constructor, arguments);
    }

    public static Object instanceThrows(final Class<?> clazz) throws Throwable {
        return INSTANCE.initThrows(ClassUtil.getConstructor(clazz));
    }

    public static Object instanceThrows(final Constructor<?> constructor, final Object... arguments) throws Throwable {
        return INSTANCE.initThrows(constructor, arguments);
    }

    public static Object invokeStatic(final Method method, final Object... arguments) {
        return INSTANCE.execute(null, method, arguments);
    }

    public static Object invoke(final Object instance, final Method method, final Object... arguments) {
        return INSTANCE.execute(instance, method, arguments);
    }

    public static Object invokeThrows(final Object instance, final Method method, final Object... arguments) throws Throwable {
        return INSTANCE.executeThrows(instance, method, arguments);
    }

    // Setter

    public static void setValue(final Object instance, final Class<?> clazz, final String fieldName, final Object value) {
        setValue(instance, ClassUtil.getField(clazz, fieldName), value);
    }

    public static void setObjectValue(final Object instance, final Class<?> clazz, final String fieldName, final Object value) {
        setObjectValue(instance, ClassUtil.getField(clazz, fieldName), value);
    }

    public static void setStaticValue(final Class<?> clazz, final String fieldName, final Object value) {
        setStaticValue(ClassUtil.getField(clazz, fieldName), value);
    }

    public static void setValue(final Object instance, final Field field, final Object value) {
        if (field == null) {
            return;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            setStaticValue(field, value);
            return;
        }
        setObjectValue(instance, field, value);
    }

    public static void setObjectValue(final Object instance, final Field field, final Object value) {
        if (instance == null || field == null) {
            return;
        }
        try {
            INSTANCE.setObjectValueSafe(instance, field, value);
        } catch (final AccessUnsuccessful unsafe) {
            INSTANCE.setObjectValueUnsafe(instance, field, value);
        }
    }

    public static void setStaticValue(final Field field, final Object value) {
        if (field == null) {
            return;
        }
        try {
            INSTANCE.setStaticValueSafe(field, value);
        } catch (final AccessUnsuccessful unsafe) {
            INSTANCE.setStaticValueUnsafe(field, value);
        }
    }

    // Getter

    public static Object getValue(final Object instance, final Class<?> clazz, final String fieldName) {
        return getValue(instance, ClassUtil.getField(clazz, fieldName));
    }

    public static Object getObjectValue(final Object instance, final Class<?> clazz, final String fieldName) {
        return getObjectValue(instance, ClassUtil.getField(clazz, fieldName));
    }

    public static Object getStaticValue(final Class<?> clazz, final String fieldName) {
        return getStaticValue(ClassUtil.getField(clazz, fieldName));
    }

    public static Object getValue(final Object instance, final Field field) {
        if (field == null) {
            return null;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return getStaticValue(field);
        }
        return getObjectValue(instance, field);
    }

    public static Object getObjectValue(final Object instance, final Field field) {
        if (instance == null || field == null) {
            return null;
        }
        try {
            return INSTANCE.getObjectValueSafe(instance, field);
        } catch (final AccessUnsuccessful unsafe) {
            return INSTANCE.getObjectValueUnsafe(instance, field);
        }
    }

    public static Object getStaticValue(final Field field) {
        if (field == null) {
            return null;
        }
        try {
            return INSTANCE.getStaticValueSafe(field);
        } catch (final AccessUnsuccessful unsafe) {
            return INSTANCE.getStaticValueUnsafe(field);
        }
    }

    /*
     * Static Accessors
     */

    public static VarHandle accessField(final Field field) {
        return INSTANCE.handle(field, false);
    }

    public static VarHandle accessField(final Field field, final boolean forceModification) {
        return INSTANCE.handle(field, forceModification);
    }

    public static MethodHandle accessFieldGetter(final Field field) {
        return INSTANCE.handleGetter(field);
    }

    public static MethodHandle accessFieldSetter(final Field field) {
        return INSTANCE.handleSetter(field);
    }

    public static MethodHandle accessMethod(final Method method) {
        return INSTANCE.handle(method);
    }

    public static MethodHandle accessConstructor(final Constructor<?> constructor) {
        return INSTANCE.handle(constructor);
    }

    /*
     * Static Utilities
     */

    /*
     * Internal Exceptions
     */

    private static final class AccessUnsuccessful extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

}