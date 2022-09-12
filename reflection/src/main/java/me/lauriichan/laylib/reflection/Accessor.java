package me.lauriichan.laylib.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class Accessor {

    public static final Accessor INVALID = new Accessor();

    static final AccessorCache CACHE = new AccessorCache();

    public static Accessor of(Class<?> owner) {
        return CACHE.get(owner);
    }

    public static Accessor ofNullable(Class<?> owner) {
        if (owner == null) {
            return INVALID;
        }
        return CACHE.get(owner);
    }

    private final Class<?> owner;

    private final ConcurrentHashMap<String, Method> methods = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Field> fields = new ConcurrentHashMap<>();

    private Accessor() {
        this.owner = null;
    }

    Accessor(Class<?> owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    /*
     * Management
     */

    public boolean valid() {
        return owner != null;
    }

    public void delete() {
        if (valid()) {
            CACHE.delete(owner);
        }
        clear();
    }

    public Class<?> getOwner() {
        return owner;
    }

    public Collection<Method> getMethods() {
        return methods.values();
    }

    public Collection<Field> getFields() {
        return fields.values();
    }

    public List<Method> getMethods(final String baseName) {
        String base = baseName + '#';
        int index = 0;
        ArrayList<Method> methods = new ArrayList<>();
        String name;
        while (this.methods.containsKey(name = base + (index++))) {
            methods.add(this.methods.get(name));
        }
        return methods;
    }

    public List<Field> getFields(final String baseName) {
        String base = baseName + '#';
        int index = 0;
        ArrayList<Field> fields = new ArrayList<>();
        String name;
        while (this.fields.containsKey(name = base + (index++))) {
            fields.add(this.fields.get(name));
        }
        return fields;
    }

    public Method getMethod(String name) {
        return methods.get(name);
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public boolean hasMethod(final String name) {
        return methods.containsKey(name);
    }

    public boolean hasField(final String name) {
        return fields.containsKey(name);
    }

    public void putMethod(final String name, final Method method) {
        if (!valid() || methods.containsKey(name) || !method.getDeclaringClass().isAssignableFrom(owner)) {
            return;
        }
        methods.put(name, method);
    }

    public void putField(final String name, final Field field) {
        if (!valid() || fields.containsKey(name) || !field.getDeclaringClass().isAssignableFrom(owner)) {
            return;
        }
        fields.put(name, field);
    }

    public void removeMethod(final String name) {
        methods.remove(name);
    }

    public void removeField(final String name) {
        fields.remove(name);
    }

    public void clear() {
        methods.clear();
        fields.clear();
    }

    public void clearMethods() {
        methods.clear();
    }

    public void clearFields() {
        fields.clear();
    }

    /*
     * Reflections
     */

    public Class<?> findNestedClass(final String name) {
        if (!valid()) {
            return null;
        }
        return ClassUtil.findClass(owner, name);
    }

    public Constructor<?> findConstructor(final Class<?>... parameters) {
        if (!valid()) {
            return null;
        }
        return ClassUtil.getConstructor(owner, parameters);
    }

    public Object initialize(Object... arguments) {
        if (!valid()) {
            return null;
        }
        return InstanceBuilder.create(owner, arguments);
    }

    public Object getValue(final String name) {
        return getValue(null, name);
    }

    public Object getValue(final Object instance, final String name) {
        Field field = fields.get(name);
        if (field == null) {
            return null;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return JavaAccess.getStaticValue(field);
        }
        if (instance == null) {
            return null;
        }
        return JavaAccess.getObjectValue(instance, fields.get(name));
    }

    public void setValue(final String name, final Object value) {
        setValue(null, name, value);
    }

    public void setValue(final Object instance, final String name, final Object value) {
        Field field = fields.get(name);
        if (field == null) {
            return;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            JavaAccess.setStaticValue(field, value);
            return;
        }
        if (instance == null) {
            return;
        }
        JavaAccess.setObjectValue(instance, field, value);
    }

    public Object invoke(final String name, final Object... arguments) {
        return invoke(null, name, arguments);
    }

    public Object invoke(final Object instance, final String name, final Object... arguments) {
        Method method = methods.get(name);
        if (method == null || method.getParameterCount() != arguments.length) {
            return null;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            return JavaAccess.invokeStatic(method, arguments);
        }
        if (instance == null) {
            return null;
        }
        return JavaAccess.invoke(instance, method, arguments);
    }

    /*
     * Search
     */

    public Accessor findMethod(final String name, final String methodName, final Class<?>... parameters) {
        if (!valid() || methods.containsKey(name)) {
            return this;
        }
        Method method = ClassUtil.getMethod(owner, methodName, parameters);
        if (method != null) {
            methods.put(name, method);
        }
        return this;
    }

    public Accessor findMethod(final String name, final Class<?>... parameters) {
        return findMethod(name, 0, parameters);
    }

    public Accessor findMethod(final String name, int index, final Class<?>... parameters) {
        if (!valid() || methods.containsKey(name)) {
            return this;
        }
        Method[] methods = ClassUtil.getMethods(owner);
        if (methods.length == 0) {
            return this;
        }
        int idx = 0;
        for (final Method method : methods) {
            Class<?>[] methodParameters = method.getParameterTypes();
            if (methodParameters.length != parameters.length || !hasSameArguments(methodParameters, parameters)) {
                continue;
            }
            if (idx++ != index) {
                continue;
            }
            this.methods.put(name, method);
        }
        return this;
    }

    public Accessor findMethods(final String baseName, final Class<?>... parameters) {
        if (!valid()) {
            return this;
        }
        Method[] methods = ClassUtil.getMethods(owner);
        if (methods.length == 0) {
            return this;
        }
        String base = baseName + '#';
        int index = 0;
        for (final Method method : methods) {
            Class<?>[] methodParameters = method.getParameterTypes();
            if (methodParameters.length != parameters.length || !hasSameArguments(methodParameters, parameters)) {
                continue;
            }
            String name = base + (index++);
            if (this.methods.containsKey(name)) {
                continue;
            }
            this.methods.put(name, method);
        }
        return this;
    }

    public Accessor findField(final String name, final String fieldName) {
        if (!valid() || fields.containsKey(name)) {
            return this;
        }
        Field field = ClassUtil.getField(owner, fieldName);
        if (field != null) {
            fields.put(name, field);
        }
        return this;
    }

    public Accessor findField(final String name, final Class<?> type) {
        return findField(name, 0, type);
    }

    public Accessor findField(final String name, int index, final Class<?> type) {
        if (!valid() || this.fields.containsKey(name)) {
            return this;
        }
        Field[] fields = ClassUtil.getFields(owner);
        if (fields.length == 0) {
            return this;
        }
        int idx = 0;
        for (final Field field : fields) {
            if (!field.getType().equals(type)) {
                continue;
            }
            if (idx++ != index) {
                continue;
            }
            this.fields.put(name, field);
        }
        return this;
    }

    public Accessor findFields(final String baseName, final Class<?> type) {
        if (!valid()) {
            return this;
        }
        Field[] fields = ClassUtil.getFields(owner);
        if (fields.length == 0) {
            return this;
        }
        String base = baseName + '#';
        int index = 0;
        for (final Field field : fields) {
            if (!field.getType().equals(type)) {
                continue;
            }
            String name = base + (index++);
            if (this.fields.containsKey(name)) {
                continue;
            }
            this.fields.put(name, field);
        }
        return this;
    }

    private static boolean hasSameArguments(Class<?>[] compare1, Class<?>[] compare2) {
        if (compare1.length == 0 && compare2.length == 0) {
            return true;
        } else if (compare1.length != compare2.length) {
            return false;
        }
        for (Class<?> arg1 : compare1) {
            boolean found = true;
            for (Class<?> arg2 : compare2) {
                if (!arg1.isAssignableFrom(arg2)) {
                    found = false;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}