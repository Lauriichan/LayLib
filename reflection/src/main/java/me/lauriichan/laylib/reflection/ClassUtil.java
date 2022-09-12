package me.lauriichan.laylib.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public final class ClassUtil {

    private ClassUtil() {
        throw new UnsupportedOperationException();
    }

    /*
     * Primitives
     */

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TO_COMPLEX, COMPLEX_TO_PRIMITIVE;

    static {
        Map<Class<?>, Class<?>> collect = new HashMap<>(10);
        collect.put(byte.class, Byte.class);
        collect.put(boolean.class, Boolean.class);
        collect.put(short.class, Short.class);
        collect.put(int.class, Integer.class);
        collect.put(long.class, Long.class);
        collect.put(float.class, Float.class);
        collect.put(double.class, Double.class);
        collect.put(String.class, String.class);
        collect.put(void.class, Void.class);
        collect.put(char.class, Character.class);
        PRIMITIVE_TO_COMPLEX = Collections.unmodifiableMap(collect);
        collect = new HashMap<>(10);
        for (Entry<Class<?>, Class<?>> entry : PRIMITIVE_TO_COMPLEX.entrySet()) {
            collect.put(entry.getValue(), entry.getKey());
        }
        COMPLEX_TO_PRIMITIVE = Collections.unmodifiableMap(collect);
    }

    public static Class<?> asClass(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Class) {
            return (Class<?>) object;
        }
        return object.getClass();
    }

    public static boolean isWrappedType(Class<?> type) {
        return PRIMITIVE_TO_COMPLEX.containsKey(type) || COMPLEX_TO_PRIMITIVE.containsKey(type);
    }

    public static boolean isWrappedObject(Object object) {
        return isWrappedType(asClass(object));
    }

    public static boolean isPrimitiveType(Class<?> type) {
        return PRIMITIVE_TO_COMPLEX.containsKey(type);
    }

    public static boolean isPrimitiveObject(Object object) {
        return isPrimitiveType(asClass(object));
    }

    public static boolean isComplexType(Class<?> type) {
        return COMPLEX_TO_PRIMITIVE.containsKey(type);
    }

    public static boolean isComplexObject(Object object) {
        return isComplexType(asClass(object));
    }

    public static Class<?> toComplexType(Class<?> type) {
        return PRIMITIVE_TO_COMPLEX.getOrDefault(type, type);
    }

    public static Class<?> toPrimitiveType(Class<?> type) {
        return COMPLEX_TO_PRIMITIVE.getOrDefault(type, type);
    }

    /*
     * Reflection
     */

    public static String getClassName(final Class<?> clazz) {
        final String name = clazz.getSimpleName();
        if (name.contains(".")) {
            return name.split("\\.")[0];
        }
        return name;
    }

    public static Field getField(final Class<?> clazz, final String field) {
        if (clazz == null || field == null) {
            return null;
        }
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException | SecurityException ignore) {
            try {
                return clazz.getField(field);
            } catch (NoSuchFieldException | SecurityException ignore0) {
                return null;
            }
        }
    }

    public static Field[] getFields(final Class<?> clazz) {
        final Field[] field0 = clazz.getFields();
        final Field[] field1 = clazz.getDeclaredFields();
        final HashSet<Field> fields = new HashSet<>();
        Collections.addAll(fields, field0);
        Collections.addAll(fields, field1);
        return fields.toArray(new Field[fields.size()]);
    }

    public static Field getStaticField(final Class<?> clazz, final Class<?> returnType) {
        return getField(clazz, true, returnType);
    }

    public static Field getDeclaredField(final Class<?> clazz, final Class<?> returnType) {
        return getField(clazz, false, returnType);
    }

    public static Field getField(final Class<?> clazz, final boolean isStatic, final Class<?> returnType) {
        if (clazz == null) {
            return null;
        }
        final Field[] fields = getFields(clazz);
        for (final Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) != isStatic || !field.getType().equals(returnType)) {
                continue;
            }
            return field;
        }
        return null;
    }

    public static Method getMethod(final Class<?> clazz, final String method, final Class<?>... arguments) {
        if (clazz == null || method == null) {
            return null;
        }
        try {
            return clazz.getDeclaredMethod(method, arguments);
        } catch (NoSuchMethodException | SecurityException ignore) {
            try {
                return clazz.getMethod(method, arguments);
            } catch (NoSuchMethodException | SecurityException ignore0) {
                return null;
            }
        }
    }

    public static Method[] getMethods(final Class<?> clazz) {
        final Method[] method0 = clazz.getMethods();
        final Method[] method1 = clazz.getDeclaredMethods();
        final HashSet<Method> methods = new HashSet<>();
        Collections.addAll(methods, method0);
        Collections.addAll(methods, method1);
        return methods.toArray(new Method[methods.size()]);
    }

    public static Method getDeclaredMethod(final Class<?> clazz, final Class<?> returnType, final Class<?>... arguments) {
        return getMethod(clazz, false, returnType, Collections.emptyList(), arguments);
    }

    public static Method getDeclaredMethod(final Class<?> clazz, final Class<?> returnType, final List<String> blacklisted,
        final Class<?>... arguments) {
        return getMethod(clazz, false, returnType, blacklisted, arguments);
    }

    public static Method getStaticMethod(final Class<?> clazz, final Class<?> returnType, final Class<?>... arguments) {
        return getMethod(clazz, true, returnType, Collections.emptyList(), arguments);
    }

    public static Method getStaticMethod(final Class<?> clazz, final Class<?> returnType, final List<String> blacklisted,
        final Class<?>... arguments) {
        return getMethod(clazz, true, returnType, blacklisted, arguments);
    }

    public static Method getMethod(final Class<?> clazz, final boolean isStatic, final Class<?> returnType, final List<String> blacklisted,
        final Class<?>... arguments) {
        if (clazz == null) {
            return null;
        }
        final Method[] methods = getMethods(clazz);
        mainLoop:
        for (final Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) != isStatic || !method.getReturnType().equals(returnType)
                || blacklisted.contains(method.getName())) {
                continue;
            }
            final Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != arguments.length) {
                continue;
            }
            for (int index = 0; index < parameters.length; index++) {
                if (!parameters[index].isAssignableFrom(arguments[index])) {
                    continue mainLoop;
                }
            }
            return method;
        }
        return null;
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... arguments) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredConstructor(arguments);
        } catch (NoSuchMethodException | SecurityException ignore) {
            try {
                return clazz.getConstructor(arguments);
            } catch (NoSuchMethodException | SecurityException ignore0) {
                return null;
            }
        }
    }

    public static Constructor<?>[] getConstructors(final Class<?> clazz) {
        final Constructor<?>[] constructor0 = clazz.getConstructors();
        final Constructor<?>[] constructor1 = clazz.getDeclaredConstructors();
        final HashSet<Constructor<?>> constructors = new HashSet<>();
        Collections.addAll(constructors, constructor0);
        Collections.addAll(constructors, constructor1);
        return constructors.toArray(new Constructor[constructors.size()]);

    }

    public static Class<?> findClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException | LinkageError e) {
            return null;
        }
    }

    public static Class<?> findClass(final Class<?> clazz, final String name) {
        if (clazz == null || name == null) {
            return null;
        }
        final int size = clazz.getClasses().length + clazz.getDeclaredClasses().length;
        if (size == 0) {
            return null;
        }
        final Class<?>[] classes = new Class<?>[size];
        final Class<?>[] tmp = clazz.getClasses();
        System.arraycopy(tmp, 0, classes, 0, tmp.length);
        System.arraycopy(clazz.getDeclaredClasses(), tmp.length, classes, tmp.length, size - tmp.length);
        for (int i = 0; i < size; i++) {
            String target = classes[i].getSimpleName();
            if (target.contains(".")) {
                target = target.split(".", 2)[0];
            }
            if (target.equals(name)) {
                return classes[i];
            }
        }
        return null;
    }

    public static boolean hasAnnotation(final AnnotatedElement element, final Class<? extends Annotation> annotationType) {
        return element.isAnnotationPresent(annotationType);
    }

    public static <A extends Annotation> A getAnnotation(final AnnotatedElement element, final Class<A> annotationType) {
        final A annotation = element.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        return element.getDeclaredAnnotation(annotationType);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A[] getAnnotations(final AnnotatedElement element, final Class<A> annotationType) {
        final A[] annotation0 = element.getAnnotationsByType(annotationType);
        final A[] annotation1 = element.getDeclaredAnnotationsByType(annotationType);
        if (annotation0.length != 0 && annotation1.length != 0) {
            final HashSet<A> annotations = new HashSet<>();
            Collections.addAll(annotations, annotation0);
            Collections.addAll(annotations, annotation1);
            return annotations.toArray((A[]) Array.newInstance(annotationType, annotations.size()));
        }
        if (annotation0.length == 0) {
            return annotation1;
        }
        return annotation0;
    }

    public static <A extends Annotation> Optional<A> getOptionalAnnotation(final AnnotatedElement element, final Class<A> annotationType) {
        return Optional.ofNullable(getAnnotation(element, annotationType));
    }

    public static <E extends Throwable> Optional<E> findException(final Throwable throwable, Class<E> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isAssignableFrom(current.getClass())) {
                return Optional.of(type.cast(current));
            }
            current = current.getCause();
        }
        return Optional.empty();
    }

}
