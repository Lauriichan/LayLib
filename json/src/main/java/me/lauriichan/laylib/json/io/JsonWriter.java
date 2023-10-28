package me.lauriichan.laylib.json.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.IJsonNumber;
import me.lauriichan.laylib.json.JsonArray;
import me.lauriichan.laylib.json.JsonBoolean;
import me.lauriichan.laylib.json.JsonNull;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.json.JsonString;

public final class JsonWriter {

    private static final String[] ESCAPE_CHARS;

    static {
        ESCAPE_CHARS = new String[128];
        ESCAPE_CHARS['\b'] = "\\b";
        ESCAPE_CHARS['\f'] = "\\f";
        ESCAPE_CHARS['\n'] = "\\n";
        ESCAPE_CHARS['\r'] = "\\r";
        ESCAPE_CHARS['\t'] = "\\t";
        ESCAPE_CHARS['\"'] = "\\\"";
        ESCAPE_CHARS['\\'] = "\\\\";
        for (int code = 0; code < 31; code++) {
            ESCAPE_CHARS[code] = String.format("\\u%04x", code);
        }
    }

    public static final int TAB_SPACES = 4;

    private boolean pretty = false;
    private boolean spaces = false;
    private int indent = 1;
    
    /*
     * Settings
     */

    public boolean isPretty() {
        return pretty;
    }

    public JsonWriter setPretty(boolean pretty) {
        this.pretty = pretty;
        return this;
    }

    public boolean usesSpaces() {
        return spaces;
    }

    public JsonWriter setSpaces(boolean spaces) {
        this.spaces = spaces;
        return this;
    }

    public int getIndent() {
        return indent;
    }

    public JsonWriter setIndent(int indent) {
        this.indent = indent;
        return this;
    }

    public JsonWriter setTabIndent(int indent) {
        this.indent = indent * TAB_SPACES;
        return this;
    }
    
    /*
     * Serializer methods
     */
    
    public String toString(IJson<?> value) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            writeValue(value, writer, 0);
            return writer.toString();
        }
    }
    
    public byte[] toBytes(IJson<?> value) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            toStream(value, stream);
            return stream.toByteArray();
        }
    }
    
    public void toWriter(IJson<?> value, Writer writer) throws IOException {
        writeValue(value, writer, 0);
    }
    
    public void toStream(IJson<?> value, OutputStream stream) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
            writeValue(value, writer, 0);
        }
    }
    
    public void toFile(IJson<?> value, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writeValue(value, writer, 0);
        }
    }
    
    public void toPath(IJson<?> value, Path path) throws IOException {
        try (OutputStream stream = path.getFileSystem().provider().newOutputStream(path, StandardOpenOption.CREATE)) {
            toStream(value, stream);
        }
    }
    
    /*
     * Writing
     */

    private void writeEntry(Map.Entry<String, IJson<?>> entry, Writer writer, int depth) throws IOException {
        if (pretty) {
            indent(writer, depth);
        }
        writeStringObject(entry.getKey(), writer);
        writer.append(':');
        if (pretty) {
            writer.append(' ');
        }
        writeValue(entry.getValue(), writer, depth);
    }

    private void writeValue(IJson<?> value, Writer writer, int depth) throws IOException {
        switch (value.type()) {
        case NULL:
            writeNull((JsonNull) value, writer);
            break;
        case ARRAY:
            writeArray((JsonArray) value, writer, depth);
            break;
        case OBJECT:
            writeObject((JsonObject) value, writer, depth);
            break;
        case STRING:
            writeString((JsonString) value, writer);
            break;
        case BOOLEAN:
            writeBoolean((JsonBoolean) value, writer);
            break;
        case BYTE:
        case SHORT:
        case INTEGER:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BIG_INTEGER:
        case BIG_DECIMAL:
            writeNumber((IJsonNumber<?>) value, writer);
            break;
        case NUMBER:
            break;
        default:
            break;
        }
    }

    private void writeObject(JsonObject object, Writer writer, int depth) throws IOException {
        writer.append('{');
        int size = object.size();
        if (size != 0) {
            if (pretty) {
                writer.append('\n');
            }
            int current = 0;
            int deep = depth + 1;
            for (Map.Entry<String, IJson<?>> entry : object) {
                writeEntry(entry, writer, deep);
                if (++current != size) {
                    writer.append(',');
                }
                if (pretty) {
                    writer.append('\n');
                }
            }
            if (pretty) {
                indent(writer, depth);
            }
        }
        writer.append('}');
    }

    private void writeArray(JsonArray array, Writer writer, int depth) throws IOException {
        writer.append('[');
        int size = array.size();
        if (size != 0) {
            if (pretty) {
                writer.append('\n');
            }
            int current = 0;
            int deep = depth + 1;
            for (IJson<?> value : array) {
                if (pretty) {
                    indent(writer, deep);
                }
                writeValue(value, writer, deep);
                if (++current != size) {
                    writer.append(',');
                }
                if (pretty) {
                    writer.append('\n');
                }
            }
            if (pretty) {
                indent(writer, depth);
            }
        }
        writer.append(']');
    }

    private void writeString(JsonString string, Writer writer) throws IOException {
        writeStringObject(string.value(), writer);
    }

    private void writeNumber(IJsonNumber<?> number, Writer writer) throws IOException {
        writer.append(number.value().toString());
    }

    private void writeNull(JsonNull jsonNull, Writer writer) throws IOException {
        writer.append("null");
    }

    private void writeBoolean(JsonBoolean jsonBoolean, Writer writer) throws IOException {
        writer.append(jsonBoolean.value().toString());
    }

    /*
     * Helpers
     */

    private void indent(Writer writer, int depth) throws IOException {
        int amount = indent * depth;
        char append = spaces ? ' ' : '\t';
        for (int count = 0; count < amount; count++) {
            writer.append(append);
        }
    }

    private void writeStringObject(String string, Writer writer) throws IOException {
        char[] array = string.toCharArray();
        StringBuilder builder = new StringBuilder("\"");
        for (int index = 0; index < array.length; index++) {
            char character = array[index];
            if (character < 128) {
                String escaped = ESCAPE_CHARS[character];
                if (escaped != null) {
                    builder.append(escaped);
                    continue;
                }
            }
            if (character == '\u2028') {
                builder.append("\\u2028");
                continue;
            }
            if (character == '\u2029') {
                builder.append("\\u2029");
                continue;
            }
            builder.append(character);
        }
        writer.append(builder.append('"').toString());
    }

}
