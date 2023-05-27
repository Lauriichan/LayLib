package me.lauriichan.laylib.command;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import me.lauriichan.laylib.command.argument.provider.CommandManagerProvider;
import me.lauriichan.laylib.command.util.LockedList;
import me.lauriichan.laylib.command.util.Reference;
import me.lauriichan.laylib.command.util.Triple;
import me.lauriichan.laylib.localization.Key;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;

public final class CommandManager {

    private static final DecimalFormat PERCENTAGE = new DecimalFormat("0.00%");

    private final ArgumentRegistry registry;

    private final ConcurrentHashMap<String, NodeCommand> commands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, CommandProcess> processes = new ConcurrentHashMap<>();

    private final Reference<ICommandInjector> injector = Reference.of();

    private final ISimpleLogger logger;

    private String prefix = "/";

    public CommandManager() {
        this(ISimpleLogger.NOP, new ArgumentRegistry());
    }

    public CommandManager(final ArgumentRegistry registry) {
        this(ISimpleLogger.NOP, registry);
    }

    public CommandManager(final ISimpleLogger logger) {
        this(logger, new ArgumentRegistry());
    }

    public CommandManager(final ISimpleLogger logger, final ArgumentRegistry registry) {
        this.logger = Objects.requireNonNull(logger, "Logger can't be null!");
        this.registry = Objects.requireNonNull(registry, "Registry can't be null!");
        registry.setProvider(new CommandManagerProvider(this));
    }

    public ArgumentRegistry getRegistry() {
        return registry;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
    
    public ISimpleLogger getLogger() {
        return logger;
    }

    public CommandManager setInjector(ICommandInjector injector) {
        this.injector.set(injector);
        return this;
    }

    public ICommandInjector getInjector() {
        return injector.get();
    }

    public CommandProcess cancelProcess(Actor<?> actor) {
        CommandProcess process = processes.remove(actor.getId());
        if (process == null) {
            actor.sendTranslatedMessage("command.process.cancel.unavailable");
            return null;
        }
        actor.sendTranslatedMessage("command.process.cancel.success", Key.of("command", process.getLabel()));
        return process;
    }

    public CommandProcess createProcess(Actor<?> actor, String label, String[] args) {
        args = Arrays.stream(args).filter(Predicate.not(String::isBlank)).toArray(String[]::new);
        NodeCommand command = getCommand(label);
        if (command == null) {
            actor.sendTranslatedMessage("command.process.create.no-command", Key.of("label", label));
            return null;
        }
        if (command.isRestricted() && !actor.hasPermission(command.getPermission())) {
            actor.sendTranslatedMessage("command.process.not-permitted", Key.of("permission", command.getPermission()));
            return null;
        }
        Entry<Node, String> nodePack = findNode(command.getNode(), prefix + label.toLowerCase(), 0, args);
        Node node = nodePack.getKey();
        if (node.getAction() == null) {
            actor.sendTranslatedMessage("command.process.create.no-action", Key.of("command", nodePack.getValue()));
            return null;
        }
        NodeAction action = node.getAction();
        if(action.isRestricted() && !actor.hasPermission(action.getPermission())) {
            actor.sendTranslatedMessage("command.process.not-permitted", Key.of("permission", action.getPermission()));
            return null;
        }
        CommandProcess process = new CommandProcess(nodePack.getValue(), action, command.getInstance());
        NodeArgument argument = process.findNext(actor);
        if (argument == null) {
            executeProcess(actor, process);
            return process;
        }
        this.processes.put(actor.getId(), process);
        actor.sendTranslatedMessage("command.process.create.success", Key.of("command", nodePack.getValue()));
        sendNodeInfo(actor, process, argument);
        return process;
    }

    public void handleProcessSkip(Actor<?> actor, CommandProcess process) {
        int index = process.getIndex();
        if (!process.skip(actor)) {
            actor.sendTranslatedMessage("command.process.skip.unskippable", Key.of("command", process.getLabel()),
                Key.of("argument", process.getAction().getArguments().get(process.getIndex()).getName()));
            return;
        }
        NodeArgument argument = process.getAction().getArguments().get(index);
        actor.sendTranslatedMessage("command.process.skip.success", Key.of("command", process.getLabel()),
            Key.of("argument", argument.getName()));
        argument = process.findNext(actor);
        if (argument == null) {
            executeProcess(actor, process);
            return;
        }
        sendNodeInfo(actor, process, argument);
    }

    public void handleProcessInput(Actor<?> actor, CommandProcess process, String input) {
        handleProcessInput(actor, process, input, false);
    }

    public void handleProcessInput(Actor<?> actor, CommandProcess process, String input, boolean suggestion) {
        if (input == null) {
            return;
        }
        if (suggestion) {
            actor.sendTranslatedMessage("command.process.input.suggestion", Key.of("input", input));
        } else {
            actor.sendTranslatedMessage("command.process.input.user", Key.of("input", input));
        }
        try {
            process.provide(actor, input);
        } catch (IllegalArgumentException exp) {
            NodeArgument argument = process.findNext(actor);
            if (argument == null) {
                executeProcess(actor, process);
                return;
            }
            actor.sendTranslatedMessage("command.process.input.failed", Key.of("error", exp.getMessage()),
                Key.of("argument.type", ClassUtil.getClassName(argument.getArgumentType())));
            Suggestions suggestions = new Suggestions();
            argument.getType().suggest(actor, input, suggestions);
            if (!suggestions.hasSuggestions()) {
                return;
            }
            Entry<String, Double>[] entries = suggestions.getSuggestions(5);
            if (entries.length == 0) {
                return;
            }
            actor.sendTranslatedMessage("command.process.suggest.header");
            for (Entry<String, Double> entry : entries) {
                actor.actionMessageBuilder()
                    .message("command.process.suggest.format", Key.of("certainty", PERCENTAGE.format(entry.getValue())),
                        Key.of("suggestion", entry.getKey()))
                    .action(Action.run("/suggestion " + entry.getKey())).actionHover("command.process.suggest.hover").send(actor);
            }
            return;
        }
        NodeArgument argument = process.findNext(actor);
        if (argument == null) {
            executeProcess(actor, process);
            return;
        }
        sendNodeInfo(actor, process, argument);
    }

    public void sendProcessInfo(Actor<?> actor, CommandProcess process) {
        if (process == null) {
            return;
        }
        sendNodeInfo(actor, process, process.findNext(actor));
    }

    private void sendNodeInfo(Actor<?> actor, CommandProcess process, NodeArgument argument) {
        actor.sendTranslatedMessage("command.process.argument.specify", Key.of("argument.name", argument.getName()),
            Key.of("argument.type", argument.getArgumentType().getSimpleName()));
        actor.actionMessageBuilder().message("command.process.argument.cancelable.message").action(Action.run("/cancel"))
            .actionHover("command.process.argument.cancelable.hover").send(actor);
        if (argument.isOptional()) {
            actor.actionMessageBuilder().message("command.process.argument.optional.message").action(Action.run("/skip"))
                .actionHover("command.process.argument.optional.hover").send(actor);
        }
    }

    public boolean executeProcess(Actor<?> actor, CommandProcess process) {
        if (process == null || process.findNext(actor) != null) {
            return false;
        }
        process.executed();
        processes.remove(actor.getId()); // Remove process before executing
        NodeAction action = process.getAction();
        if(action.isRestricted() && !actor.hasPermission(action.getPermission())) {
            actor.sendTranslatedMessage("command.process.not-permitted", Key.of("permission", action.getPermission()));
            return true;
        }
        try {
            JavaAccess.invokeThrows(process.getInstance(), action.getMethod(), process.getValues());
        } catch (Throwable e) {
            actor.sendTranslatedMessage("command.process.execution.failed", Key.of("command", process.getLabel()),
                Key.of("error", e.getMessage()));
            logger.debug("Failed to execute command '{0}'", e, process.getLabel());
        }
        return true;
    }

    public CommandProcess getProcess(UUID id) {
        return processes.get(id);
    }

    public boolean register(Class<?> clazz) {
        NodeCommand command = new NodeCommand(clazz, logger, registry);
        if (commands.containsKey(command.getName())) {
            return false;
        }
        LockedList<String> list = command.getAliases();
        for (int index = 0; index < list.size(); index++) {
            String name = list.get(index);
            if (!commands.containsKey(name)) {
                commands.put(name, command);
                continue;
            }
            list.remove(index--);
        }
        list.lock();
        commands.put(command.getName(), command);
        if (injector.isPresent()) {
            injector.get().inject(command);
        }
        return true;
    }

    public boolean unregister(String name) {
        NodeCommand command = commands.remove(name);
        if (command == null) {
            return false;
        }
        for (String alias : command.getAliases()) {
            commands.remove(alias);
        }
        if (injector.isPresent()) {
            injector.get().uninject(command);
        }
        return true;
    }

    public NodeCommand getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
    
    public List<NodeCommand> getCommands() {
        return commands.values().stream().distinct().collect(Collectors.toList());
    }

    public String[] getCommandNames() {
        return commands.keySet().toArray(String[]::new);
    }

    public Triple<NodeCommand, Node, String> findNode(String commandString) {
        String[] tmp = Arrays.stream(commandString.split(" ")).filter(Predicate.not(String::isBlank)).toArray(String[]::new);
        if (tmp.length == 0) {
            return null;
        }
        String label = tmp[0];
        NodeCommand command = getCommand(label);
        if (command == null) {
            return null;
        }
        String[] args = new String[tmp.length - 1];
        System.arraycopy(tmp, 1, args, 0, args.length);
        Entry<Node, String> entry = findNode(command.getNode(), prefix + label.toLowerCase(), 0, args);
        return Triple.of(command, entry.getKey(), entry.getValue());
    }

    public Triple<NodeCommand, Node, String> findNode(String label, String pathRaw) {
        NodeCommand command = getCommand(label);
        if (command == null) {
            return null;
        }
        Entry<Node, String> entry = findNode(command.getNode(), prefix + label.toLowerCase(), 0,
            Arrays.stream(pathRaw.split(" ")).filter(Predicate.not(String::isBlank)).toArray(String[]::new));
        return Triple.of(command, entry.getKey(), entry.getValue());
    }

    public Triple<NodeCommand, Node, String> findNode(String label, String[] path) {
        NodeCommand command = getCommand(label);
        if (command == null) {
            return null;
        }
        Entry<Node, String> entry = findNode(command.getNode(), prefix + label.toLowerCase(), 0,
            Arrays.stream(path).filter(Predicate.not(String::isBlank)).toArray(String[]::new));
        return Triple.of(command, entry.getKey(), entry.getValue());
    }

    private Entry<Node, String> findNode(Node node, String label, int index, String[] path) {
        Node parent = node;
        while (index != path.length) {
            String current = path[index++].toLowerCase();
            Node found = parent.getNode(current);
            if (found == null) {
                return Map.entry(parent, label);
            }
            parent = found;
            label = label + " " + current;
        }
        return Map.entry(parent, label);
    }

}
