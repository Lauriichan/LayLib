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
        while (index < values.length) {
            NodeArgument argument = action.getArguments().get(index);
            if (!argument.isProvided()) {
                return argument;
            }
            values[argument.getArgumentIndex()] = argument.getType().parse(actor, null);
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

    public void provide(Actor<?> actor, String input) {
        NodeArgument argument = findNext(actor);
        if (argument == null) {
            return;
        }
        values[argument.getArgumentIndex()] = argument.getType().parse(actor, input);
        index++;
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
