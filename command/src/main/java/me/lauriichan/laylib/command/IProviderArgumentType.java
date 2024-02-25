package me.lauriichan.laylib.command;

public interface IProviderArgumentType<E> extends IArgumentType<E> {

    @Override
    default E parse(Actor<?> actor, String input, IArgumentMap map) throws IllegalArgumentException {
        return provide(actor);
    }

    E provide(Actor<?> actor);

}
