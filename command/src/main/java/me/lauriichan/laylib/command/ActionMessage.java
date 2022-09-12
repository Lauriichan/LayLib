package me.lauriichan.laylib.command;

import me.lauriichan.laylib.localization.IMessage;
import me.lauriichan.laylib.localization.Key;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.MessageProvider;

public final class ActionMessage {

    private Action hoverAction;
    private Action clickAction;
    private String message;

    private final String language;
    private final MessageManager messageManager;

    public ActionMessage(Actor<?> actor) {
        this(actor.getMessageManager(), actor.getLanguage());
    }

    public ActionMessage(MessageManager messageManager, String language) {
        this.messageManager = messageManager;
        this.language = language;
    }

    public ActionMessage action(Action action) {
        if (action.isClick()) {
            this.clickAction = action;
            return this;
        }
        this.hoverAction = action;
        return this;
    }

    public ActionMessage actionHoverRaw(String messageRaw) {
        return action(Action.hoverText(messageRaw));
    }

    public ActionMessage actionHover(String messageId, Key... placholders) {
        return actionHoverRaw(messageManager.translate(messageId, language, placholders));
    }

    public ActionMessage actionHover(MessageProvider messageProvider, Key... placholders) {
        return actionHoverRaw(messageManager.translate(messageProvider, language, placholders));
    }

    public ActionMessage actionHover(IMessage message, Key... placholders) {
        return actionHoverRaw(messageManager.format(message, placholders));
    }

    public ActionMessage messageRaw(String message) {
        this.message = message;
        return this;
    }

    public ActionMessage message(String messageId, Key... placholders) {
        return messageRaw(messageManager.translate(messageId, language, placholders));
    }

    public ActionMessage message(MessageProvider messageProvider, Key... placholders) {
        return messageRaw(messageManager.translate(messageProvider, language, placholders));
    }

    public ActionMessage message(IMessage message, Key... placholders) {
        return messageRaw(messageManager.format(message, placholders));
    }
    
    public Action clickAction() {
        return clickAction;
    }
    
    public Action hoverAction() {
        return hoverAction;
    }
    
    public String message() {
        return message;
    }

    public void send(Actor<?> actor) {
        actor.sendActionMessage(this);
    }

}
