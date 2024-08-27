package me.lauriichan.laylib.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import me.lauriichan.laylib.command.argument.*;
import me.lauriichan.laylib.command.argument.provider.ActorProvider;
import me.lauriichan.laylib.command.exception.NotEnoughArgumentsException;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;

public final class ArgumentRegistry {

    private final ConcurrentHashMap<Class<?>, IProviderArgumentType<?>> providerType = new ConcurrentHashMap<>();
    private final ArrayList<Class<?>> provided = new ArrayList<>();
    private final HashMap<Class<?>, ArgumentBuilder<?>> argumentTypes = new HashMap<>();
    private final ArrayList<Class<?>> types = new ArrayList<>();

    public ArgumentRegistry() {
        registerArgumentType(BooleanArgument.class);
        registerArgumentType(ByteArgument.class);
        registerArgumentType(ShortArgument.class);
        registerArgumentType(IntegerArgument.class);
        registerArgumentType(LongArgument.class);
        registerArgumentType(FloatArgument.class);
        registerArgumentType(DoubleArgument.class);
        registerArgumentType(StringArgument.class);
        registerArgumentType(EnumArgument.class);
        setProvider(ActorProvider.PROVIDER);
    }

    public void setProvider(IProviderArgumentType<?> provider) {
        if (provider == null) {
            return;
        }
        Class<?> type = extractGenericType(provider.getClass());
        if (type == null) {
            return;
        }
        if (!provided.contains(type)) {
            provided.add(type);
        }
        providerType.put(type, provider);
    }

    public IProviderArgumentType<?> getProvider(Class<?> clazz) {
        IProviderArgumentType<?> type = providerType.get(clazz);
        if (type != null) {
            return type;
        }
        for (int index = 0; index < provided.size(); index++) {
            Class<?> current = provided.get(index);
            if (!current.isAssignableFrom(clazz)) {
                continue;
            }
            return providerType.get(current);
        }
        return null;
    }

    public <V> boolean registerArgumentType(Class<? extends IArgumentType<V>> type) {
        if (type == null || IProviderArgumentType.class.isAssignableFrom(type) || type.isInterface()
            || Modifier.isAbstract(type.getModifiers())) {
            return false;
        }
        Class<?> argumentType = extractGenericType(type);
        if (argumentType == null || types.contains(argumentType)) {
            return false;
        }
        types.add(argumentType);
        argumentTypes.put(argumentType, new ArgumentBuilder<>(type));
        return true;
    }

    public ArgumentBuilder<?> getArgumentType(Class<?> type) {
        type = ClassUtil.toComplexType(type);
        ArgumentBuilder<?> builder = argumentTypes.get(type);
        if (builder != null) {
            return builder;
        }
        for (int index = 0; index < types.size(); index++) {
            Class<?> current = types.get(index);
            if (!current.isAssignableFrom(type)) {
                continue;
            }
            return argumentTypes.get(current);
        }
        return null;
    }

    public IArgumentType<?> getArgument(Class<?> type) {
        return getArgument(type, null);
    }

    public IArgumentType<?> getArgument(Class<?> type, IArgumentMap map) {
        ArgumentBuilder<?> builder = getArgumentType(type);
        if (builder == null) {
            return null;
        }
        return builder.build(map);
    }

    private Class<?> extractGenericType(Class<?> clazz) {
        Type superType = clazz.getGenericSuperclass();
        Type[] superTypeArray;
        if (superType == null) {
            superTypeArray = new Type[0];
        } else {
            superTypeArray = new Type[] {
                superType
            };
        }
        Type[] interfaces = clazz.getGenericInterfaces();
        Type[] types = new Type[superTypeArray.length + interfaces.length];
        if (types.length == 0) {
            return null;
        }
        System.arraycopy(superTypeArray, 0, types, 0, superTypeArray.length);
        System.arraycopy(interfaces, 0, types, superTypeArray.length, interfaces.length);
        for (Type type : types) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            Class<?> current = ClassUtil.findClass(((ParameterizedType) type).getRawType().getTypeName());
            if (current == null
                || !(IProviderArgumentType.class.isAssignableFrom(current) || IArgumentType.class.isAssignableFrom(current))) {
                continue;
            }
            Type out = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (out instanceof ParameterizedType) {
                out = ((ParameterizedType) out).getRawType();
            }
            return ClassUtil.findClass(out.getTypeName());
        }
        return null;
    }

    public static final class ArgumentBuilder<V> {

        private final Class<? extends IArgumentType<V>> type;
        private final Constructor<?> constructor;
        private final boolean map;

        public ArgumentBuilder(final Class<? extends IArgumentType<V>> type) {
            this.type = type;
            Constructor<?> tmp = ClassUtil.getConstructor(type, IArgumentMap.class);
            if (tmp != null) {
                this.map = true;
                this.constructor = tmp;
                return;
            }
            this.map = false;
            this.constructor = Objects.requireNonNull(ClassUtil.getConstructor(type),
                "Valid Constructor can't be found for ArgumentType '" + ClassUtil.getClassName(type) + "'!");
        }

        public Class<? extends IArgumentType<V>> getType() {
            return type;
        }

        public Constructor<?> getConstructor() {
            return constructor;
        }

        public boolean requiresMap() {
            return map;
        }

        public IArgumentType<V> build(IArgumentMap map) throws NotEnoughArgumentsException {
            try {
                if (this.map) {
                    if (map == null) {
                        map = EmptyArgumentMap.INSTANCE;
                    }
                    return type.cast(JavaAccess.PLATFORM.invoke(constructor, map));
                }
                return type.cast(JavaAccess.PLATFORM.invoke(constructor));
            } catch (Throwable e) {
                Optional<NotEnoughArgumentsException> exp = ClassUtil.findException(e, NotEnoughArgumentsException.class);
                if (exp.isEmpty()) {
                    return null;
                }
                throw (NotEnoughArgumentsException) exp.get();
            }
        }

    }

}
