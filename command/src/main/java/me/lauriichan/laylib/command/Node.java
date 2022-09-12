package me.lauriichan.laylib.command;

import java.util.HashMap;

public final class Node {

    private final Node parent;

    private final String name;
    private final NodeAction action;

    private final HashMap<String, Node> map = new HashMap<>();

    Node(final String name) {
        this(null, name, null);
    }

    Node(final Node parent, final String name) {
        this(parent, name, null);
    }

    Node(final String name, final NodeAction action) {
        this(null, name, action);
    }

    Node(final Node parent, final String name, final NodeAction action) {
        this.parent = parent;
        this.name = name;
        this.action = action;
    }

    public Node getParent() {
        return parent;
    }

    public Node getRoot() {
        if (parent == null) {
            return this;
        }
        return parent.getRoot();
    }

    public String getName() {
        return name;
    }

    public NodeAction getAction() {
        return action;
    }

    public Node getNode(String name) {
        return map.get(name);
    }

    Node putNode(String name, NodeAction action) {
        if (map.containsKey(name)) {
            return null;
        }
        Node node = new Node(this, name, action);
        map.put(name, node);
        return node;
    }

    public String[] getNames() {
        return map.keySet().toArray(String[]::new);
    }

    public boolean hasChildren() {
        return !map.isEmpty();
    }

}
