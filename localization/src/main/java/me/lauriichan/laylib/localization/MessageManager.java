package me.lauriichan.laylib.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.lauriichan.laylib.localization.source.MessageSource;

public class MessageManager {

    protected final ConcurrentHashMap<String, MessageProvider> messages = new ConcurrentHashMap<>();
    protected final int maxDepth;

    public MessageManager() {
        this(5);
    }

    public MessageManager(int maxDepth) {
        this.maxDepth = Math.max(maxDepth, 0);
    }

    public void unregisterAll() {
        messages.clear();
    }

    public boolean unregister(String id) {
        return messages.remove(id) != null;
    }

    public boolean register(MessageProvider provider) {
        if (provider == null || messages.containsKey(provider.getId())) {
            return false;
        }
        messages.put(provider.getId(), provider);
        return true;
    }

    public List<String> register(MessageSource source) {
        if (source == null) {
            return Collections.emptyList();
        }
        ArrayList<MessageProvider> providers = new ArrayList<>();
        source.provide(providers);
        ArrayList<String> failed = new ArrayList<>();
        for (MessageProvider provider : providers) {
            if (provider == null) {
                continue;
            }
            if (register(provider)) {
                failed.add(provider.getId());
            }
        }
        return Collections.unmodifiableList(failed);
    }
    
    public String[] getIds() {
        return messages.keySet().toArray(String[]::new);
    }

    public MessageProvider[] getProviders() {
        return messages.values().toArray(MessageProvider[]::new);
    }

    public MessageProvider getProvider(String messageId) {
        return messages.get(messageId);
    }

    public IMessage getMessage(String messageId, String language) {
        MessageProvider provider = getProvider(messageId);
        if (provider == null) {
            return null;
        }
        return provider.getMessage(language);
    }

    public String translate(String messageId, String language, Key... placeholders) {
        return format(getMessage(messageId, language), placeholders);
    }

    public String translate(MessageProvider provider, String language, Key... placeholders) {
        return format(provider.getMessage(language), placeholders);
    }

    public String format(IMessage message, Key... placeholders) {
        if (message == null) {
            return null;
        }
        return format(message.value(), message.language(), placeholders, -1);
    }

    protected String format(String message, String language, Key[] values, int depth) {
        if (depth > maxDepth) {
            return message;
        }
        final Placeholder[] placeholders = Placeholder.parse(message);
        if (placeholders.length == 0) {
            return message;
        }
        for (final Placeholder placeholder : placeholders) {
            if (placeholder.isMessage()) {
                final IMessage targetMessage = getMessage(placeholder.getId(), language);
                if (targetMessage == null) {
                    continue;
                }
                message = placeholder.replace(message, format(targetMessage.value(), language, values, depth + 1));
                continue;
            }
            for (final Key value : values) {
                if (!value.getKey().equals(placeholder.getId())) {
                    continue;
                }
                String content = value.getValueOrDefault("null").toString();
                if (!"null".equals(content)) {
                    content = format(content, language, values, depth + 1);
                }
                message = placeholder.replace(message, content);
                break;
            }
        }
        return message;
    }

}
