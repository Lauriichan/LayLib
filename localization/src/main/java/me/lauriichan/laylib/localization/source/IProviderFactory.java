package me.lauriichan.laylib.localization.source;

import me.lauriichan.laylib.localization.MessageProvider;

@FunctionalInterface
public interface IProviderFactory {
    
    MessageProvider build(String id, String fallback);
    
    default MessageProvider build(IMessageDefinition definition) {
        return build(definition.id(), definition.fallback());
    }

}
