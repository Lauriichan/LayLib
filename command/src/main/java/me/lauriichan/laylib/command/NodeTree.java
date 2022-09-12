package me.lauriichan.laylib.command;

import java.util.HashMap;

import me.lauriichan.laylib.command.annotation.Action;
import me.lauriichan.laylib.command.util.Reference;

final class NodeTree {

    private static final String[] EMPTY = new String[0];

    private final HashMap<String, NodeTree> map = new HashMap<>();
    private final Reference<NodeAction> action = Reference.of();

    public String[] getNames() {
        return map.keySet().toArray(String[]::new);
    }

    public NodeTree getTree(String name) {
        return map.get(name);
    }

    public NodeAction getAction() {
        return action.get();
    }

    public void add(Action[] actions, NodeAction nodeAction) {
        for (Action action : actions) {
            String fullPath = action.value().trim().replace("  ", " ").toLowerCase();
            if (fullPath.isEmpty()) {
                add(0, EMPTY, fullPath, nodeAction);
                continue;
            }
            add(0, fullPath.split(" "), fullPath, nodeAction);
        }
    }

    private void add(int index, String[] path, String fullPath, NodeAction action) {
        if (index == path.length) {
            if (this.action.isPresent()) {
                throw new IllegalStateException("Two nodes try to use '" + fullPath + "'!");
            }
            this.action.set(action);
            return;
        }
        String current = path[index];
        NodeTree tree = map.get(current);
        if (tree == null) {
            tree = new NodeTree();
            map.put(current, tree);
        }
        tree.add(index + 1, path, fullPath, action);
    }

    public Node build(String name) {
        Node node = new Node(name, action.get());
        append(node);
        return node;
    }

    private void append(Node node) {
        for (String current : map.keySet()) {
            map.get(current).append(node, current);
        }
    }

    private void append(Node parent, String name) {
        Node node = parent.putNode(name, action.get());
        if (node == null) {
            return;
        }
        append(node);
    }

}
