package me.lauriichan.laylib.reflection;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class AccessorProvider {

    private boolean deleted = false;

    private final ConcurrentHashMap<Class<?>, String> names = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Accessor> accessors = new ConcurrentHashMap<>();

    public AccessorProvider() {
        Accessor.CACHE.listen(this);
    }

    public void delete() {
        if (deleted) {
            return;
        }
        deleted = true;
        Accessor.CACHE.listen(this);
        clear();
    }

    public Optional<Accessor> get(String name) {
        return Optional.ofNullable(accessors.get(name));
    }

    public Accessor getOrNull(String name) {
        return accessors.get(name);
    }

    public Accessor create(String name, Class<?> clazz) {
        Accessor accessor = accessors.get(name);
        if (accessor != null) {
            return accessor;
        }
        if (clazz == null) {
            return Accessor.INVALID;
        }
        accessors.put(name, accessor = Accessor.of(clazz));
        names.put(clazz, name);
        return accessor;
    }

    void clear() {
        names.clear();
        accessors.clear();
    }

    void remove(Class<?> owner) {
        String name = names.remove(owner);
        if (name == null) {
            return;
        }
        accessors.remove(name);
    }

}