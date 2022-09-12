package me.lauriichan.laylib.command.argument.provider;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IProviderArgumentType;

public final class ActorProvider implements IProviderArgumentType<Actor<?>> {

    public static final ActorProvider PROVIDER = new ActorProvider();

    private ActorProvider() {}

    @Override
    public Actor<?> provide(Actor<?> actor) {
        return actor;
    }

}
