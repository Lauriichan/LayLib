package me.lauriichan.laylib.json.io;

public final class JsonSyntaxException extends Exception {

    private static final long serialVersionUID = -461425017365081360L;

    public JsonSyntaxException() {}

    public JsonSyntaxException(final String message) {
        super(message);
    }

    public JsonSyntaxException(final String message, final Throwable cause) {
        super(message, cause);
    }

}