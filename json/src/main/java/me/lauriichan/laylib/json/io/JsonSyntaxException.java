package me.lauriichan.laylib.json.io;

public final class JsonSyntaxException extends Exception {

    private static final long serialVersionUID = -461425017365081360L;

    public JsonSyntaxException() {
        super();
    }

    public JsonSyntaxException(String message) {
        super(message);
    }

    public JsonSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

}