package me.lauriichan.laylib.localization;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class MessageProcess {

    private final MessageManager messageManager;

    private final String originalMessage, language;
    private final Object2ObjectArrayMap<String, String> placeholder;

    private final IntArrayList depthStack;

    private final int maxDepth;

    private volatile String resolvedMessage;

    public MessageProcess(MessageManager messageManager, String message, String language, Key[] values, int maxDepth) {
        if (messageManager == null) {
            throw new IllegalStateException("No MessageManager available");
        }
        this.messageManager = messageManager;
        this.originalMessage = message;
        this.language = language;
        this.placeholder = new Object2ObjectArrayMap<>(values.length * 2);
        for (Key value : values) {
            placeholder.put(value.getKey(), value.getValueOrDefault("null").toString());
        }
        this.depthStack = new IntArrayList((messageManager.processors().size() + 1) * maxDepth);
        this.maxDepth = maxDepth;
        if (messageManager.processors().isEmpty()) {
            this.resolvedMessage = message;
        }
    }
    
    public String translateMessage(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        final IMessage message = messageManager.getMessage(id, language);
        if (message == null) {
            return null;
        }
        return process(message.value());
    }

    public String originalMessage() {
        return originalMessage;
    }

    public String language() {
        return language;
    }

    public void set(String id, Object object) {
        placeholder.put(id, object == null ? "null" : object.toString());
    }

    public void set(String id, String content) {
        placeholder.put(id, content == null ? "null" : content);
    }

    public void remove(String id) {
        placeholder.remove(id);
    }

    public String getOrDefault(String id, String fallback) {
        return placeholder.getOrDefault(id, fallback);
    }

    public String get(String id) {
        return placeholder.get(id);
    }

    public final String process() {
        if (resolvedMessage != null) {
            return resolvedMessage;
        }
        if (!depthStack.isEmpty()) {
            throw new IllegalArgumentException("Processor is currently processing.");
        }
        depthStack.push(-1);
        try {
            return resolvedMessage = process(originalMessage);
        } finally {
            depthStack.clear();
        }
    }

    public final String process(String message) {
        if (resolvedMessage != null || depthStack.isEmpty()) {
            throw new IllegalStateException("Call 'process()' to get the resolved message");
        }
        if (message == null) {
            return message;
        }
        int depth = depthStack.topInt();
        if (depth >= maxDepth) {
            return message;
        }
        for (IMessageProcessor processor : messageManager.processors()) {
            depthStack.push(depth + 1);
            try {
                message = processor.process(this, message);
            } finally {
                depthStack.popInt();
            }
        }
        return message;
    }

}
