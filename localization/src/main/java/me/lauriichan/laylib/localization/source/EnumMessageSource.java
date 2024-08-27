package me.lauriichan.laylib.localization.source;

import java.util.Collections;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.localization.MessageProvider;

public final class EnumMessageSource extends MessageSource {

    private final MessageProvider[] providers;

    public <E extends Enum<E> & IMessageDefinition> EnumMessageSource(Class<E> clazz, IProviderFactory factory) {
        super(factory);
        E[] constants = clazz.getEnumConstants();
        this.providers = new MessageProvider[constants.length];
        for (int index = 0; index < constants.length; index++) {
            providers[index] = factory.build(constants[index]);
        }
    }

    @Override
    public void provide(ObjectArrayList<MessageProvider> providers) {
        Collections.addAll(providers, this.providers);
    }

}
