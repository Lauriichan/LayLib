package me.lauriichan.laylib.logger.util;

import java.util.ArrayList;
import java.util.List;

public final class StringUtil {

    private StringUtil() {
        throw new UnsupportedOperationException();
    }

    public static String format(String message, Object[] placeholders) {
        if (placeholders.length == 0) {
            return message;
        }
        StringBuilder output = new StringBuilder();
        char[] chars = message.toCharArray();
        StringBuilder buffer = new StringBuilder();
        int state = 0;
        for (int index = 0; index < chars.length; index++) {
            char current = chars[index];
            switch (current) {
            case '{':
                if (state != 0) {
                    output.append('{');
                    if (buffer.length() != 0) {
                        output.append(buffer);
                        buffer = new StringBuilder();
                    }
                }
                state = 1;
                break;
            case '}':
                if (state != 2) {
                    output.append('{');
                    if (buffer.length() != 0) {
                        output.append(buffer);
                        buffer = new StringBuilder();
                    }
                    state = 0;
                    break;
                }
                try {
                    int idx = Integer.parseInt(buffer.toString());
                    if (idx >= placeholders.length || idx < 0) {
                        output.append('{').append(buffer).append('}');
                        break;
                    }
                    output.append(placeholders[idx]);
                    break;
                } catch (NumberFormatException nfe) {
                    output.append('{').append(buffer).append('}');
                    break;
                } finally {
                    buffer = new StringBuilder();
                    state = 0;
                }
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                if (state == 0) {
                    output.append(current);
                    break;
                }
                state = 2;
                buffer.append(current);
                break;
            default:
                output.append(current);
                break;
            }
        }
        return output.toString();
    }
    
    public static String shortClassName(Class<?> clazz) {
        return shortenClassName(clazz.getName());
    }

    public static String shortenClassName(String name) {
        if (!name.contains(".")) {
            return name;
        }
        String[] parts = name.split("\\.");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (part.length() > 2) {
                part = part.substring(0, 2);
            }
            builder.append(part).append('.');
        }
        return builder.append(parts[parts.length - 1]).toString();
    }

    public static String stackTraceToString(Throwable throwable) {
        return stackTraceToBuilder(throwable).toString();
    }

    public static StringBuilder stackTraceToBuilder(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        stackTraceToBuilder(throwable, builder, false);
        return builder;
    }

    public static String[] stackTraceToArray(Throwable throwable) {
        ArrayList<String> list = stackTraceToList(throwable);
        return list.toArray(new String[list.size()]);
    }

    public static ArrayList<String> stackTraceToList(Throwable throwable) {
        ArrayList<String> list = new ArrayList<>();
        stackTraceToList(throwable, list, false);
        return list;
    }

    public static void stackTraceToList(Throwable throwable, List<String> list) {
        stackTraceToList(throwable, list, false);
    }

    private static void stackTraceToList(Throwable throwable, List<String> list, boolean cause) {
        StringBuilder stack = new StringBuilder();
        if (cause) {
            stack.append('\n').append("Caused by: ");
        }
        stack.append(throwable.getClass().getName()).append(": ").append(throwable.getLocalizedMessage());
        list.add(stack.toString());
        stack = new StringBuilder();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String fileName = element.getFileName();
            stack.append("\n").append('\t').append("at ").append(element.getClassName()).append('.').append(element.getMethodName())
                .append('(');
            if (fileName == null) {
                stack.append("Unknown Source");
            } else {
                stack.append(fileName).append(':').append(Integer.toString(element.getLineNumber()));
            }
            list.add(stack.append(')').toString());
            stack = new StringBuilder();
        }
        Throwable caused = throwable.getCause();
        if (caused != null) {
            stackTraceToList(caused, list, true);
        }
    }

    private static void stackTraceToBuilder(Throwable throwable, StringBuilder builder, boolean cause) {
        StringBuilder stack = new StringBuilder();
        if (cause) {
            stack.append('\n').append("Caused by: ");
        }
        stack.append(throwable.getClass().getName()).append(": ").append(throwable.getLocalizedMessage());
        builder.append(stack.toString());
        stack = new StringBuilder();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String fileName = element.getFileName();
            stack.append("\n").append('\t').append("at ").append(element.getClassName()).append('.').append(element.getMethodName())
                .append('(');
            if (fileName == null) {
                stack.append("Unknown Source");
            } else {
                stack.append(fileName).append(':').append(Integer.toString(element.getLineNumber()));
            }
            builder.append(stack.append(')').toString());
            stack = new StringBuilder();
        }
        Throwable caused = throwable.getCause();
        if (caused != null) {
            stackTraceToBuilder(caused, builder, true);
        }
    }

}
