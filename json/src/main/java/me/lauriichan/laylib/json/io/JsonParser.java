package me.lauriichan.laylib.json.io;

import java.io.IOException;
import java.io.StringReader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.json.*;

public final class JsonParser {

    private JsonParser() {
        throw new UnsupportedOperationException("Parser class doesn't need to be initialized");
    }

    public static IJson<?> fromString(String string) throws IOException, JsonSyntaxException, IllegalStateException {
        try (JsonReader reader = new JsonReader(new StringReader(string))) {
            return read(reader);
        }
    }

    private static IJson<?> read(JsonReader reader) throws IOException, JsonSyntaxException, IllegalStateException {
        ObjectArrayList<IJson<?>> stack = new ObjectArrayList<>();
        ObjectArrayList<String> keyStack = new ObjectArrayList<>();
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
                if (key != null) {
                    keyStack.push(key);
                    key = null;
                }
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
                throw new IllegalStateException("Unexpected token: %s".formatted(token));
            }
            if (state != 2 && key != null) {
                throw new IllegalStateException("Unexpected key: %s".formatted(key));
            }
            if (state == 0 && !stack.isEmpty()) {
                IJson<?> next = stack.top();
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
