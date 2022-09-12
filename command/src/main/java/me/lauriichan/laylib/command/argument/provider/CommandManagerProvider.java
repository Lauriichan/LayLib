package me.lauriichan.laylib.command.argument.provider;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.CommandManager;
import me.lauriichan.laylib.command.IProviderArgumentType;

public final class CommandManagerProvider implements IProviderArgumentType<CommandManager> {

    private final CommandManager commandManager;

    public CommandManagerProvider(final CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public CommandManager provide(Actor<?> actor) {
        return commandManager;
    }

}
