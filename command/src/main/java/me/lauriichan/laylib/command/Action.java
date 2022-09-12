package me.lauriichan.laylib.command;

import java.util.Objects;

public final class Action {

    public static enum ActionType {
        HOVER_SHOW,
        HOVER_TEXT,
        CLICK_RUN,
        CLICK_SUGGEST,
        CLICK_URL,
        CLICK_COPY,
        CLICK_FILE;

        public boolean isHover() {
            return this == HOVER_TEXT || this == HOVER_SHOW;
        }

        public boolean isClick() {
            return this != HOVER_TEXT;
        }
    }

    private final ActionType type;
    private final Object value;

    private Action(final ActionType type, final Object value) {
        this.type = Objects.requireNonNull(type, "ActionType can't be null!");
        this.value = Objects.requireNonNull(value, "Object value can't be null!");
        if (value instanceof String && ((String) value).isBlank()) {
            throw new IllegalArgumentException("String value can't be blank");
        }
    }

    public ActionType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
    
    public String getValueAsString() {
        return value.toString();
    }

    public boolean isHover() {
        return type.isHover();
    }

    public boolean isClick() {
        return type.isClick();
    }

    public static Action hoverShow(Object object) {
        return new Action(ActionType.HOVER_SHOW, object);
    }

    public static Action hoverText(String text) {
        return new Action(ActionType.HOVER_TEXT, text);
    }

    public static Action run(String text) {
        return new Action(ActionType.CLICK_RUN, text);
    }

    public static Action suggest(String text) {
        return new Action(ActionType.CLICK_SUGGEST, text);
    }

    public static Action url(String url) {
        return new Action(ActionType.CLICK_URL, url);
    }

    public static Action copy(String text) {
        return new Action(ActionType.CLICK_COPY, text);
    }

    public static Action file(String path) {
        return new Action(ActionType.CLICK_FILE, path);
    }

}
