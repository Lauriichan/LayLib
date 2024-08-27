package me.lauriichan.laylib.localization.source;

import java.util.Objects;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.localization.MessageProvider;

public abstract class MessageSource {

    protected final IProviderFactory factory;

    public MessageSource(final IProviderFactory factory) {
        this.factory = Objects.requireNonNull(factory, "ProviderFactory can't be null!");
    }

    public IProviderFactory getFactory() {
        return factory;
    }

    public abstract void provide(ObjectArrayList<MessageProvider> providers);

}
