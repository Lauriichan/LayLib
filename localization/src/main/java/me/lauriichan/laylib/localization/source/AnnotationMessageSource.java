package me.lauriichan.laylib.localization.source;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;

public final class AnnotationMessageSource extends MessageSource {

    private final MessageProvider[] providers;

    public AnnotationMessageSource(Class<?> clazz, IProviderFactory factory) {
        super(factory);
        ObjectArrayList<MessageProvider> providers = new ObjectArrayList<>();
        Field[] fields = ClassUtil.getFields(clazz);
        Collector<CharSequence, ?, String> collector = Collectors.joining("\n");
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Message message = ClassUtil.getAnnotation(field, Message.class);
            if (message == null) {
                continue;
            }
            MessageProvider provider = factory.build(message.id(), Arrays.stream(message.content()).collect(collector));
            providers.add(provider);
            Class<?> fieldType = field.getType();
            Class<?> providerType = provider.getClass();
            if (!fieldType.isAssignableFrom(providerType)) {
                continue;
            }
            JavaAccess.PLATFORM.setValue(field, provider);
        }
        this.providers = providers.toArray(MessageProvider[]::new);
    }

    @Override
    public void provide(ObjectArrayList<MessageProvider> providers) {
        Collections.addAll(providers, this.providers);
    }

}
