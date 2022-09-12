package me.lauriichan.laylib.reflection;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

final class AccessorCache {

    private final ConcurrentHashMap<Class<?>, Accessor> accessors = new ConcurrentHashMap<>();
    private final ArrayList<AccessorProvider> providers = new ArrayList<>();

    AccessorCache() {}

    final void listen(AccessorProvider provider) {
        if (providers.contains(provider)) {
            providers.remove(provider);
            return;
        }
        providers.add(provider);
    }

    public final Accessor get(Class<?> owner) {
        if (owner == null) {
            return null;
        }
        Accessor access = accessors.get(owner);
        if (access != null) {
            return access;
        }
        access = new Accessor(owner);
        accessors.put(owner, access);
        return access;
    }

    public final void delete(Class<?> owner) {
        accessors.remove(owner);
        for (AccessorProvider provider : providers) {
            provider.remove(owner);
        }
    }

    public final void clear(ClassLoader loader) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (Class<?> clazz : accessors.keySet()) {
            if (!clazz.getClassLoader().equals(loader)) {
                continue;
            }
            classes.add(clazz);
        }
        for (Class<?> clazz : classes) {
            accessors.remove(clazz);
            for (AccessorProvider provider : providers) {
                provider.remove(clazz);
            }
        }
    }

    public final void clear() {
        accessors.clear();
        for (AccessorProvider provider : providers) {
            provider.clear();
        }
    }

}