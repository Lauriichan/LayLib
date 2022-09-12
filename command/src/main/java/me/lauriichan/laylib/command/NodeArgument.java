package me.lauriichan.laylib.command;

public final class NodeArgument {

    public static enum NodeArgumentType {
        INPUT_REQUIRED,
        INPUT_OPTIONAL,
        PROVIDED;
    }

    private final int index;
    private final NodeArgumentType nodeType;

    private final String name;
    private final IArgumentType<?> type;

    private final int argumentIndex;
    private final Class<?> argumentType;

    NodeArgument(int index, NodeArgumentType nodeType, String name, IArgumentType<?> type, int argumentIndex, Class<?> argumentType) {
        this.index = index;
        this.nodeType = nodeType;
        this.name = name;
        this.type = type;
        this.argumentIndex = argumentIndex;
        this.argumentType = argumentType;
    }

    public int getIndex() {
        return index;
    }

    public boolean isOptional() {
        return nodeType == NodeArgumentType.INPUT_OPTIONAL;
    }

    public boolean isProvided() {
        return nodeType == NodeArgumentType.PROVIDED;
    }

    public String getName() {
        return name;
    }

    public IArgumentType<?> getType() {
        return type;
    }

    public int getArgumentIndex() {
        return argumentIndex;
    }

    public Class<?> getArgumentType() {
        return argumentType;
    }

}
