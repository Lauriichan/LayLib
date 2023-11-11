package me.lauriichan.laylib.json.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.JsonArray;
import me.lauriichan.laylib.json.JsonBigDecimal;
import me.lauriichan.laylib.json.JsonBigInteger;
import me.lauriichan.laylib.json.JsonBoolean;
import me.lauriichan.laylib.json.JsonByte;
import me.lauriichan.laylib.json.JsonDouble;
import me.lauriichan.laylib.json.JsonFloat;
import me.lauriichan.laylib.json.JsonInteger;
import me.lauriichan.laylib.json.JsonLong;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.json.JsonShort;
import me.lauriichan.laylib.json.JsonString;

public final class JsonParser {

    private JsonParser() {
        throw new UnsupportedOperationException("Parser class doesn't need to be initialized");
    }

    /*
     * Reader methods
     */

    public static IJson<?> fromString(final String string) throws IOException, JsonSyntaxException, IllegalStateException {
        try (StringReader reader = new StringReader(string)) {
            return fromReader(reader);
        }
    }

    public static IJson<?> fromBytes(final byte[] bytes) throws IOException, JsonSyntaxException, IllegalStateException {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            return fromStream(stream);
        }
    }

    public static IJson<?> fromStream(final InputStream stream) throws IOException, JsonSyntaxException, IllegalStateException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return fromReader(reader);
        }
    }

    public static IJson<?> fromReader(final Reader reader) throws IOException, JsonSyntaxException, IllegalStateException {
        try (JsonReader jsonReader = new JsonReader(reader)) {
            return read(jsonReader);
        }
    }

    public static IJson<?> fromFile(final File file) throws IOException, JsonSyntaxException, IllegalStateException {
        try (FileReader reader = new FileReader(file)) {
            return fromReader(reader);
        }
    }

    public static IJson<?> fromPath(final Path path) throws IOException, JsonSyntaxException, IllegalStateException {
        try (InputStream stream = path.getFileSystem().provider().newInputStream(path, StandardOpenOption.READ)) {
            return fromStream(stream);
        }
    }

    /*
     * Reading
     */

    public static IJson<?> read(final JsonReader reader) throws IOException, JsonSyntaxException, IllegalStateException {
        final ObjectArrayList<IJson<?>> stack = new ObjectArrayList<>();
        final ObjectArrayList<String> keyStack = new ObjectArrayList<>();
        String key = null;
        JsonToken token;
        IJson<?> json = IJson.NULL;
        int state = 0; // 0 = none / 1 = object / 2 = array
        loop:
        while (true) {
            if (!reader.hasNext()) {
                if (stack.isEmpty()) {
                    return json;
                }
                return stack.pop();
            }
            token = reader.next();
            switch (token) {
            case START_OBJECT:
                if (key != null) {
                    keyStack.push(key);
                    key = null;
                }
                reader.beginObject();
                stack.push(new JsonObject());
                state = 2;
                continue loop;
            case KEY:
                if (key != null) {
                    throw new IllegalStateException();
                }
                key = reader.readName();
                continue loop;
            case END_OBJECT:
                reader.endObject();
                json = stack.pop();
                if (!keyStack.isEmpty()) {
                    key = keyStack.pop();
                    state = 2;
                } else {
                    state = 0;
                }
                break;
            case START_ARRAY:
                keyStack.push(key);
                key = null;
                reader.beginArray();
                stack.push(new JsonArray());
                state = 1;
                continue loop;
            case END_ARRAY:
                reader.endArray();
                json = stack.pop();
                if (!keyStack.isEmpty()) {
                    key = keyStack.pop();
                    state = 2;
                } else {
                    state = 0;
                }
                break;
            case NULL:
                reader.readNull();
                json = IJson.NULL;
                break;
            case STRING:
                json = new JsonString(reader.readString());
                break;
            case BOOLEAN:
                json = reader.readBoolean() ? JsonBoolean.TRUE : JsonBoolean.FALSE;
                break;
            case BYTE:
                json = JsonByte.of(reader.readByte());
                break;
            case SHORT:
                json = new JsonShort(reader.readShort());
                break;
            case INTEGER:
                json = new JsonInteger(reader.readInteger());
                break;
            case LONG:
                json = new JsonLong(reader.readLong());
                break;
            case BIG_INTEGER:
                json = new JsonBigInteger(reader.readBigInteger());
                break;
            case FLOAT:
                json = new JsonFloat(reader.readFloat());
                break;
            case DOUBLE:
                json = new JsonDouble(reader.readDouble());
                break;
            case BIG_DECIMAL:
                json = new JsonBigDecimal(reader.readBigDecimal());
                break;
            case NUMBER:
            case EOF:
            default:
                throw new IllegalStateException(String.format("Unexpected token: %s", token));
            }
            if (state != 2 && key != null) {
                throw new IllegalStateException(String.format("Unexpected key: %s", key));
            }
            if (state == 0 && !stack.isEmpty()) {
                final IJson<?> next = stack.top();
                if (next instanceof JsonArray) {
                    state = 1;
                } else if (next instanceof JsonObject) {
                    state = 2;
                }
            }
            if (key != null) {
                ((JsonObject) stack.top()).put(key, json);
                key = null;
            } else if (state == 1) {
                ((JsonArray) stack.top()).add(json);
            } else {
                return json;
            }
        }
    }

}
