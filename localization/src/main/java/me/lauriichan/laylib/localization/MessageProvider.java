package me.lauriichan.laylib.localization;

import java.util.Objects;

public abstract class MessageProvider {

    protected final String id;

    public MessageProvider(final String id) {
        this.id = Objects.requireNonNull(id, "Id can't be null").toLowerCase();
        if (id.isBlank()) {
            throw new IllegalArgumentException("Id can't be empty");
        }
    }

    /**
     * Gets the id of the messages provided
     * 
     * @return the id
     */
    public final String getId() {
        return id;
    }

    /**
     * Gets the message in a specific language
     * 
     * @param  language the language of the message
     * 
     * @return          the message for the language or the fallback language
     */
    public abstract IMessage getMessage(String language);

}
