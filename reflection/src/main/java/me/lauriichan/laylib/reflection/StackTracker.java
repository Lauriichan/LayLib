package me.lauriichan.laylib.reflection;

import java.util.Optional;

public final class StackTracker {

    private StackTracker() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static StackTraceElement[] getStack() {
        return new Throwable().getStackTrace();
    }

    public static Optional<Class<?>> getClassFromStack(final int offset) {
        final StackTraceElement element = getStack()[3 + offset];
        if (element == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(ClassUtil.findClass(element.getClassName()));
    }

    public static Optional<Class<?>> getCallerClass() {
        return getClassFromStack(1);
    }

}