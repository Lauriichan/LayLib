package me.lauriichan.laylib.localization;

import java.util.HashMap;

public final class Placeholder {

    private final String id;
    private final String original;

    private final boolean message;

    private Placeholder(final String id, final String original) {
        this.id = (this.message = id.charAt(0) == '#') ? id.substring(1) : id;
        this.original = original;
    }

    public String getId() {
        return id;
    }

    public String getOriginal() {
        return original;
    }

    public boolean isMessage() {
        return message;
    }

    public String replace(final String message, final String value) {
        return message.replace(original, value);
    }

    public static Placeholder[] parse(final String message) {
        final int length = message.length();
        StringBuilder builder = null;
        boolean escaped = false;
        final HashMap<String, Placeholder> placeholders = new HashMap<>();
        for (int index = 0; index < length; index++) {
            final char character = message.charAt(index);
            if (character == '\\') {
                escaped = !escaped;
            }
            if (character == '$') {
                if (!escaped) {
                    if (builder != null) {
                        build(placeholders, builder.toString());
                    }
                    builder = new StringBuilder().append(character);
                    continue;
                } else {
                    escaped = false;
                }
            }
            if (builder == null) {
                continue;
            }
            if (Character.isAlphabetic(character) || Character.isDigit(character) || character == '.' || character == '-' || character == '_' || character == '#') {
                builder.append(character);
                continue;
            }
            if (index + 1 < length) {
                char nextChar = message.charAt(index + 1);
                if (nextChar == ';') {
                    builder.append(nextChar);
                    index++;
                }
            }
            build(placeholders, builder.toString());
            builder = null;
            escaped = false;
        }
        if (builder != null) {
            build(placeholders, builder.toString());
        }
        return placeholders.values().toArray(new Placeholder[placeholders.size()]);
    }

    private static void build(final HashMap<String, Placeholder> map, final String original) {
        String id = original.substring(1);
        if (id.isEmpty()) {
            return;
        }
        if (id.charAt(id.length() - 1) == ';') {
            id = original.substring(0, id.length() - 1);
        }
        if (id.isEmpty() || id.length() == 1 && id.charAt(0) == '#' || map.containsKey(id)) {
            return;
        }
        map.put(id, new Placeholder(id, original));
    }

}