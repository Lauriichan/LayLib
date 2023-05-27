package me.lauriichan.laylib.command;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import me.lauriichan.laylib.command.NodeArgument.NodeArgumentType;
import me.lauriichan.laylib.command.annotation.Action;
import me.lauriichan.laylib.command.annotation.Argument;
import me.lauriichan.laylib.command.annotation.Command;
import me.lauriichan.laylib.command.annotation.Permission;
import me.lauriichan.laylib.command.exception.NotEnoughArgumentsException;
import me.lauriichan.laylib.command.util.LockedList;
import me.lauriichan.laylib.command.util.NodeHelper;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;

public final class NodeCommand {

    private final Node node;

    private final Class<?> owner;
    private final Object instance;

    private final String name;
    private final String description;
    private final String permission;
    private final LockedList<String> aliases;

    NodeCommand(Class<?> owner, ISimpleLogger logger, ArgumentRegistry registry) {
        this.owner = Objects.requireNonNull(owner, "No owner specified");
        this.instance = Objects.requireNonNull(JavaAccess.instance(owner), "Couldn't initialize '" + ClassUtil.getClassName(owner) + "'!");
        Command command = Objects.requireNonNull(ClassUtil.getAnnotation(owner, Command.class), "Command annotation is null");
        String tmpName = command.name().toLowerCase().trim().replace("  ", " ").replace(' ', '_');
        Permission permissionInfo = ClassUtil.getAnnotation(owner, Permission.class);
        this.permission = permissionInfo == null ? null : permissionInfo.value();
        if (tmpName.isEmpty()) {
            throw new IllegalArgumentException("Name of command '" + ClassUtil.getClassName(owner) + "' is empty!");
        }
        this.name = tmpName;
        this.description = command.description();
        this.aliases = new LockedList<>(command.aliases());
        Method[] methods = ClassUtil.getMethods(owner);
        NodeTree tree = new NodeTree();
        loop:
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Action[] actions = ClassUtil.getAnnotations(method, Action.class);
            if (actions == null || actions.length == 0) {
                continue;
            }
            Parameter[] parameters = method.getParameters();
            ArrayList<NodeArgument> arguments = new ArrayList<>();
            for (int index = 0; index < parameters.length; index++) {
                Parameter parameter = parameters[index];
                Class<?> type = parameter.getType();
                int idx = parameters.length - index;
                IProviderArgumentType<?> provider = registry.getProvider(type);
                if (provider != null) {
                    arguments.add(processProviderArgument(parameter, provider, type, index, idx));
                    continue;
                }
                NodeArgument argument = processInputArgument(logger, registry, parameter, type, index, idx);
                if (argument == null) {
                    logger.warning("Couldn't process action '{0}' of command '{2}' because of argument at index {1}!", actions[0].value(),
                        index, name);
                    continue loop;
                }
                arguments.add(argument);
            }
            arguments.sort(command.forceOptionalArgsLast() ? NodeHelper.sorterOptionalLast() : NodeHelper.sorter());
            tree.add(actions,
                new NodeAction(method, arguments.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(arguments)));
        }
        this.node = tree.build(name);
    }

    private NodeArgument processProviderArgument(Parameter parameter, IProviderArgumentType<?> provider, Class<?> type, int index,
        int idx) {
        String name = parameter.getName();
        Argument argument = ClassUtil.getAnnotation(parameter, Argument.class);
        if (argument != null) {
            if (!argument.name().isBlank()) {
                name = argument.name();
            }
            if (argument.index() >= 0) {
                idx = argument.index();
            }
        }
        return new NodeArgument(idx, NodeArgumentType.PROVIDED, name, provider, index, type);
    }

    private NodeArgument processInputArgument(ISimpleLogger logger, ArgumentRegistry registry, Parameter parameter, Class<?> type,
        int index, int idx) {
        IArgumentMap argumentMap = EmptyArgumentMap.INSTANCE;
        String name = parameter.getName();
        NodeArgumentType nodeType = NodeArgumentType.INPUT_REQUIRED;
        Argument argument = ClassUtil.getAnnotation(parameter, Argument.class);
        if (argument != null) {
            if (!argument.name().isBlank()) {
                name = argument.name();
            }
            if (argument.index() >= 0) {
                idx = argument.index();
            }
            if (argument.optional()) {
                nodeType = NodeArgumentType.INPUT_OPTIONAL;
            }
            argumentMap = NodeHelper.paramsToMap(argument.params());
        }
        IArgumentType<?> argumentType;
        try {
            argumentType = registry.getArgument(type, argumentMap);
            if (argumentType == null) {
                logger.debug("No argument type available for '{0}'", type.getName());
                return null;
            }
        } catch (NotEnoughArgumentsException exp) {
            logger.debug(exp);
            return null;
        }
        return new NodeArgument(idx, nodeType, name, argumentType, index, type);
    }

    public Node getNode() {
        return node;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isRestricted() {
        return permission != null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LockedList<String> getAliases() {
        return aliases;
    }

    public Class<?> getOwner() {
        return owner;
    }

    public Object getInstance() {
        return instance;
    }

}
