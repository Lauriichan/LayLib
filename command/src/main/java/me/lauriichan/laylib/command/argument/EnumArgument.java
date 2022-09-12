package me.lauriichan.laylib.command.argument;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IArgumentMap;
import me.lauriichan.laylib.command.IArgumentType;
import me.lauriichan.laylib.command.Suggestions;
import me.lauriichan.laylib.command.exception.ArgumentStack;
import me.lauriichan.laylib.command.exception.NotEnoughArgumentsException;
import me.lauriichan.laylib.command.util.LevenshteinDistance;

@SuppressWarnings("rawtypes")
public final class EnumArgument implements IArgumentType<Enum<?>> {

    private final String[] values;
    private final Class<? extends Enum> type;

    public EnumArgument(IArgumentMap map) throws NotEnoughArgumentsException {
        ArgumentStack stack = new ArgumentStack();
        Class<?> type = map.getClassOrStack("type", Enum.class, stack);
        if (type == null) {
            throw new NotEnoughArgumentsException(stack);
        }
        this.type = type.asSubclass(Enum.class);
        this.values = Arrays.stream(this.type.getEnumConstants()).map(Enum::name).map(String::toLowerCase).toArray(String[]::new);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum<?> parse(Actor<?> actor, String input) throws IllegalArgumentException {
        try {
            return Enum.valueOf(type, input.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("No enum with name '" + input.toLowerCase() + "'!");
        }
    }

    @Override
    public void suggest(Actor<?> actor, String input, Suggestions suggestions) {
        List<Entry<String, Integer>> list = LevenshteinDistance.rankByDistance(input, values);
        double max = list.stream().map(Entry::getValue).collect(Collectors.summingInt(Integer::intValue));
        for (int index = 0; index < list.size(); index++) {
            Entry<String, Integer> entry = list.get(index);
            suggestions.suggest(1 - (entry.getValue().doubleValue() / max), entry.getKey());
        }
    }

}
