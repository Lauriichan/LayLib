package me.lauriichan.laylib.command;

import me.lauriichan.laylib.command.exception.ArgumentStack;
import me.lauriichan.laylib.command.util.Option;

public interface IArgumentMap {
    
    boolean has(String key);
    
    boolean has(String key, Class<?> type);
    
    Option<Object> get(String key);
    
    <E> Option<E> get(String key, Class<E> type);
    
    Option<Class<?>> getClass(String key);
    
    <E> Option<Class<? extends E>> getClass(String key, Class<E> abstraction);
    
    default <E> Class<? extends E> getClassOrStack(String key, Class<E> abstraction, ArgumentStack stack) {
        return getClass(key, abstraction).orElseRun(() -> stack.push(key, abstraction.getClass()));
    }
    
    default <E> E getOrStack(String key, Class<E> type, ArgumentStack stack) {
        return get(key, type).orElseRun(() -> stack.push(key, type));
    }
    
    IArgumentMap set(String key, Object value);
    
    IArgumentMap remove(String key);
    
    IArgumentMap clear();
    
    IArgumentMap clone();
    
    boolean isEmpty();
    
    int size();

}
