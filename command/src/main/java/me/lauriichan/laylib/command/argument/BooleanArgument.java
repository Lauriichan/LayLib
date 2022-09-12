package me.lauriichan.laylib.command.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IArgumentType;
import me.lauriichan.laylib.command.Suggestions;
import me.lauriichan.laylib.command.util.LevenshteinDistance;

public final class BooleanArgument implements IArgumentType<Boolean> {

    private static final List<String> FALSE = Arrays.asList("no", "off", "false");
    private static final List<String> TRUE = Arrays.asList("yes", "on", "true");
    private static final List<String> ALL = new ArrayList<>(TRUE);

    static {
        ALL.addAll(FALSE);
    }

    @Override
    public Boolean parse(Actor<?> actor, String input) throws IllegalArgumentException {
        input = input.toLowerCase();
        if (TRUE.contains(input)) {
            return true;
        }
        if (FALSE.contains(input)) {
            return false;
        }
        throw new IllegalArgumentException("Unknown state");
    }

    @Override
    public void suggest(Actor<?> actor, String input, Suggestions suggestions) {
        List<Entry<String, Integer>> list = LevenshteinDistance.rankByDistance(input.toLowerCase(), ALL);
        double max = list.stream().map(Entry::getValue).collect(Collectors.summingInt(Integer::intValue));
        for (int index = 0; index < list.size(); index++) {
            Entry<String, Integer> entry = list.get(index);
            suggestions.suggest(1 - (entry.getValue().doubleValue() / max), entry.getKey());
        }
    }

}
