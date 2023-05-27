package me.lauriichan.laylib.command;

import java.util.UUID;

import me.lauriichan.laylib.localization.IMessage;
import me.lauriichan.laylib.localization.IMessageReceiver;
import me.lauriichan.laylib.localization.Key;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.MessageProvider;

public abstract class Actor<P> implements IMessageReceiver {

    public static final UUID IMPL_ID = new UUID(0, 0);
    public static final String DEFAULT_LANGUAGE = "en-uk";

    @SuppressWarnings({
        "rawtypes",
        "unchecked"
    })
    private static final Actor EMPTY = new Actor(null, null) {
        @Override
        public UUID getId() {
            return null;
        }

        @Override
        public String getLanguage() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    };

    protected final P handle;
    protected final MessageManager messageManager;

    public Actor(P handle, MessageManager messageManager) {
        this.handle = handle;
        this.messageManager = messageManager;
    }

    public final P getHandle() {
        return handle;
    }

    public final MessageManager getMessageManager() {
        return messageManager;
    }

    @SuppressWarnings("unchecked")
    public final <C> Actor<C> as(Class<C> type) {
        if (handle != null && type.isAssignableFrom(handle.getClass())) {
            return (Actor<C>) this;
        }
        return (Actor<C>) EMPTY;
    }

    public final boolean isValid() {
        return handle != null;
    }

    /*
     * Implementation dependent
     */

    public abstract String getName();

    @Override
    public String getLanguage() {
        return DEFAULT_LANGUAGE;
    }

    public String getTranslatedMessageAsString(MessageProvider provider, Key... placeholders) {
        return messageManager.translate(provider, getLanguage(), placeholders);
    }

    public String getTranslatedMessageAsString(String messageId, Key... placeholders) {
        return messageManager.translate(messageId, getLanguage(), placeholders);
    }

    public IMessage getTranslatedMessage(MessageProvider provider) {
        return provider.getMessage(getLanguage());
    }

    public IMessage getTranslatedMessage(String messageId) {
        return messageManager.getMessage(messageId, getLanguage());
    }

    public void sendMessage(String message) {}

    public void sendMessage(IMessage message, Key... placeholders) {
        sendMessage(messageManager.format(message, placeholders));
    }

    public void sendTranslatedMessage(MessageProvider provider, Key... placeholders) {
        sendMessage(messageManager.translate(provider, getLanguage(), placeholders));
    }

    public void sendTranslatedMessage(String messageId, Key... placeholders) {
        sendMessage(messageManager.translate(messageId, getLanguage(), placeholders));
    }

    public ActionMessage actionMessageBuilder() {
        return new ActionMessage(this);
    }

    public void sendActionMessage(ActionMessage message) {
        sendMessage(message.message());
    }

    public final void sendActionMessage(String message, Action... actions) {
        ActionMessage builder = actionMessageBuilder().messageRaw(message);
        for (Action action : actions) {
            builder.action(action);
        }
        sendActionMessage(builder);
    }

    public boolean hasPermission(String permission) {
        return false;
    }

}
