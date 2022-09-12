package me.lauriichan.laylib.reflection;

import java.lang.reflect.Constructor;

public final class InstanceBuilder {

    private InstanceBuilder() {
        throw new UnsupportedOperationException();
    }

    public static <T> T create(final Class<T> clazz, final Object... arguments) {
        if (arguments.length == 0) {
            return clazz.cast(JavaAccess.instance(clazz));
        }
        final Constructor<?>[] constructors = ClassUtil.getConstructors(clazz);
        final Class<?>[] classes = new Class<?>[arguments.length];
        for (int index = 0; index < arguments.length; index++) {
            classes[index] = arguments[index].getClass();
        }
        final int max = classes.length;
        Constructor<?> builder = null;
        int args = 0;
        int[] argIdx = new int[max];
        for (final Constructor<?> constructor : constructors) {
            final int count = constructor.getParameterCount();
            if (count > max || count < args) {
                continue;
            }
            final int[] tmpIdx = new int[max];
            for (int idx = 0; idx < max; idx++) {
                tmpIdx[idx] = -1;
            }
            final Class<?>[] types = constructor.getParameterTypes();
            int tmpArgs = 0;
            for (int index = 0; index < count; index++) {
                for (int idx = 0; idx < max; idx++) {
                    if (!types[index].isAssignableFrom(classes[idx])) {
                        continue;
                    }
                    tmpIdx[idx] = index;
                    tmpArgs++;
                }
            }
            if (tmpArgs != count) {
                continue;
            }
            argIdx = tmpIdx;
            args = tmpArgs;
            builder = constructor;
        }
        if (builder == null) {
            return null;
        }
        if (args == 0) {
            return clazz.cast(JavaAccess.instance(builder));
        }
        final Object[] parameters = new Object[args];
        for (int idx = 0; idx < max; idx++) {
            if (argIdx[idx] == -1) {
                continue;
            }
            parameters[argIdx[idx]] = arguments[idx];
        }
        return clazz.cast(JavaAccess.instance(builder, parameters));
    }

    public static <T> T createThrows(final Class<T> clazz, final Object... arguments) throws Throwable {
        if (arguments.length == 0) {
            return clazz.cast(JavaAccess.instanceThrows(clazz));
        }
        final Constructor<?>[] constructors = ClassUtil.getConstructors(clazz);
        final Class<?>[] classes = new Class<?>[arguments.length];
        for (int index = 0; index < arguments.length; index++) {
            classes[index] = arguments[index].getClass();
        }
        final int max = classes.length;
        Constructor<?> builder = null;
        int args = 0;
        int[] argIdx = new int[max];
        for (final Constructor<?> constructor : constructors) {
            final int count = constructor.getParameterCount();
            if (count > max || count < args) {
                continue;
            }
            final int[] tmpIdx = new int[max];
            for (int idx = 0; idx < max; idx++) {
                tmpIdx[idx] = -1;
            }
            final Class<?>[] types = constructor.getParameterTypes();
            int tmpArgs = 0;
            for (int index = 0; index < count; index++) {
                for (int idx = 0; idx < max; idx++) {
                    if (!types[index].isAssignableFrom(classes[idx])) {
                        continue;
                    }
                    tmpIdx[idx] = index;
                    tmpArgs++;
                }
            }
            if (tmpArgs != count) {
                continue;
            }
            argIdx = tmpIdx;
            args = tmpArgs;
            builder = constructor;
        }
        if (builder == null) {
            return null;
        }
        if (args == 0) {
            return clazz.cast(JavaAccess.instanceThrows(builder));
        }
        final Object[] parameters = new Object[args];
        for (int idx = 0; idx < max; idx++) {
            if (argIdx[idx] == -1) {
                continue;
            }
            parameters[argIdx[idx]] = arguments[idx];
        }
        return clazz.cast(JavaAccess.instanceThrows(builder, parameters));
    }

}