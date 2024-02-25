package me.lauriichan.laylib.command;

import me.lauriichan.laylib.command.util.NodeHelper;

public final class CommandProcess {

    private final String label;
    private final NodeAction action;
    private final Object instance;

    private final Object[] values;
    private int index = 0;

    private boolean executed = false;

    public CommandProcess(String label, NodeAction action, Object instance) {
        this.label = label;
        this.action = action;
        this.instance = instance;
        this.values = new Object[action.getArgumentCount()];
    }

    void executed() {
        this.executed = true;
    }

    public NodeArgument findNext(Actor<?> actor) {
        return findNext(actor, EmptyArgumentMap.INSTANCE);
    }

    public NodeArgument findNext(Actor<?> actor, IArgumentMap map) {
        while (index < values.length) {
            NodeArgument argument = action.getArguments().get(index);
            if (!argument.isProvided()) {
                return argument;
            }
            values[argument.getArgumentIndex()] = argument.getType().parse(actor, null, map);
            index++;
        }
        return null;
    }

    public boolean skip(Actor<?> actor) {
        NodeArgument argument = findNext(actor);
        if (argument != null && argument.isOptional()) {
            values[argument.getArgumentIndex()] = NodeHelper.nullValueFor(argument.getArgumentType());
            index++;
            return true;
        }
        return false;
    }

    public void provide(Actor<?> actor, String input, IArgumentMap map) {
        NodeArgument argument = findNext(actor);
        if (argument == null) {
            return;
        }
        values[argument.getArgumentIndex()] = argument.getType().parse(actor, input, map);
        index++;
    }

    public void provide(Actor<?> actor, String input) {
        provide(actor, input, EmptyArgumentMap.INSTANCE);
    }

    public String getLabel() {
        return label;
    }

    public NodeAction getAction() {
        return action;
    }

    public Object[] getValues() {
        return values;
    }

    public int getIndex() {
        return index;
    }

    public Object getInstance() {
        return instance;
    }

    public boolean isExecuted() {
        return executed;
    }

}
