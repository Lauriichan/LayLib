package me.lauriichan.laylib.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class JavaLookup {

    public static final JavaLookup PLATFORM = new JavaLookup();

    private final Lookup lookup;

    private JavaLookup() {
        if (PLATFORM != null) {
            throw new UnsupportedOperationException("Only one platform instance allowed");
        }
        this.lookup = acquirePlatformLookup();
    }

    public JavaLookup(Lookup lookup) {
        this.lookup = lookup;
    }

    private Lookup acquirePlatformLookup() {
        return JavaUnsafe.UNSAFE.getValue(ClassUtil.getField(Lookup.class, "IMPL_LOOKUP"));
    }

    public final Lookup lookup() {
        return lookup;
    }

    public final JavaLookup privateIn(Class<?> type) {
        try {
            return new JavaLookup(MethodHandles.privateLookupIn(type, lookup));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to acquire private lookup for class: " + type.getName());
        }
    }

    public final JavaLookup publicIn(Class<?> type) {
        return new JavaLookup(lookup.in(type));
    }
    
    public final MethodHandle bindMethod(Object receiver, String name, MethodType methodType) {
        try {
            return lookup.bind(receiver, name, methodType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access method '" + name + "' in class: " + receiver.getClass().getName());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unknown method '" + name + "' in class: " + receiver.getClass().getName());
        }
    }

    public final MethodHandle findMethod(Class<?> type, String name, MethodType methodType) {
        try {
            return lookup.findVirtual(type, name, methodType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access method '" + name + "' in class: " + type.getName());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unknown method '" + name + "' in class: " + type.getName());
        }
    }

    public final MethodHandle findStaticMethod(Class<?> type, String name, MethodType methodType) {
        try {
            return lookup.findStatic(type, name, methodType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access method '" + name + "' in class: " + type.getName());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unknown method '" + name + "' in class: " + type.getName());
        }
    }

    public final MethodHandle findSpecialMethod(Class<?> type, String name, MethodType methodType, Class<?> specialCaller) {
        try {
            return lookup.findSpecial(type, name, methodType, specialCaller);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access method '" + name + "' in class: " + type.getName());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unknown method '" + name + "' in class: " + type.getName());
        }
    }

    public final MethodHandle findConstructor(Class<?> type, MethodType methodType) {
        try {
            return lookup.findConstructor(type, methodType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access constructor in class: " + type.getName());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unknown constructor in class: " + type.getName());
        }
    }

    public final MethodHandle findGetter(Class<?> type, String name, Class<?> fieldType) {
        try {
            return lookup.findGetter(type, name, fieldType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access field '" + name + "' in class: " + type.getName());
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Unknown field '" + name + "' in class: " + type.getName());
        }
    }

    public final MethodHandle findStaticGetter(Class<?> type, String name, Class<?> fieldType) {
        try {
            return lookup.findStaticGetter(type, name, fieldType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access field '" + name + "' in class: " + type.getName());
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Unknown field '" + name + "' in class: " + type.getName());
        }
    }
    
    public final MethodHandle findSetter(Class<?> type, String name, Class<?> fieldType) {
        try {
            return lookup.findSetter(type, name, fieldType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access field '" + name + "' in class: " + type.getName());
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Unknown field '" + name + "' in class: " + type.getName());
        }
    }

    public final MethodHandle findStaticSetter(Class<?> type, String name, Class<?> fieldType) {
        try {
            return lookup.findStaticGetter(type, name, fieldType);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access field '" + name + "' in class: " + type.getName());
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Unknown field '" + name + "' in class: " + type.getName());
        }
    }
    
    public final MethodHandle unreflect(Constructor<?> constructor) {
        try {
            return lookup.unreflectConstructor(constructor);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to unreflect constructor in class: " + constructor.getDeclaringClass().getName());
        }
    }
    
    public final MethodHandle unreflect(Method method) {
        try {
            return lookup.unreflect(method);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to unreflect method '" + method.getName() + "' in class: " + method.getDeclaringClass().getName());
        }
    }
    
    public final MethodHandle unreflect(Method method, Class<?> specialCaller) {
        try {
            return lookup.unreflectSpecial(method, specialCaller);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to unreflect method '" + method.getName() + "' in class: " + method.getDeclaringClass().getName());
        }
    }
    
    public final MethodHandle unreflectGetter(Field field) {
        try {
            return lookup.unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to unreflect field '" + field.getName() + "' in class: " + field.getDeclaringClass().getName());
        }
    }
    
    public final MethodHandle unreflectSetter(Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            JavaUnsafe.UNSAFE.unfinalize(field);
        }
        try {
            return lookup.unreflectSetter(field);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to unreflect field '" + field.getName() + "' in class: " + field.getDeclaringClass().getName());
        }
    }
    
    public final VarHandle unreflect(Field field) {
        return unreflect(field, false);
    }
    
    public final VarHandle unreflect(Field field, boolean writeAccess) {
        if (writeAccess && Modifier.isFinal(field.getModifiers())) {
            JavaUnsafe.UNSAFE.unfinalize(field);
        }
        try {
            return lookup.unreflectVarHandle(field);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to unreflect field '" + field.getName() + "' in class: " + field.getDeclaringClass().getName());
        }
    }

}
