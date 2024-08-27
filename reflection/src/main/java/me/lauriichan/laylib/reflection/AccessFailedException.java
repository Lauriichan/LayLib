package me.lauriichan.laylib.reflection;

public final class AccessFailedException extends RuntimeException {

    private static final long serialVersionUID = 195287956581060399L;

    public AccessFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
