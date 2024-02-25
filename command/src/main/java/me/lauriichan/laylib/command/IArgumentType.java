package me.lauriichan.laylib.command;

public interface IArgumentType<E> {

    E parse(Actor<?> actor, String input, IArgumentMap map) throws IllegalArgumentException;

    default void suggest(Actor<?> actor, String input, Suggestions suggestions, IArgumentMap map) {}

}
