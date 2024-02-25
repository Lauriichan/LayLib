package me.lauriichan.laylib.command.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IArgumentMap;
import me.lauriichan.laylib.command.IArgumentType;
import me.lauriichan.laylib.command.Suggestions;
import me.lauriichan.laylib.command.util.LevenshteinDistance;

public final class StringArgument implements IArgumentType<String> {

    private final boolean collection;
    private final List<String> selection;

    public StringArgument(IArgumentMap map) {
        String[] strings = map.get("collection", String[].class).get();
        if (strings == null || strings.length == 0) {
            this.selection = Collections.emptyList();
            this.collection = false;
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, strings);
        this.selection = Collections.unmodifiableList(list);
        this.collection = true;
    }

    @Override
    public String parse(Actor<?> actor, String input, IArgumentMap map) throws IllegalArgumentException {
        if (!collection) {
            return input;
        }
        if (!selection.contains(input)) {
            throw new IllegalArgumentException("Invalid input");
        }
        return input;
    }

    @Override
    public void suggest(Actor<?> actor, String input, Suggestions suggestions, IArgumentMap map) {
        if (!collection) {
            return;
        }
        List<Entry<String, Integer>> list = LevenshteinDistance.rankByDistance(input, selection);
        double max = list.stream().map(Entry::getValue).collect(Collectors.summingInt(Integer::intValue));
        for (int index = 0; index < list.size(); index++) {
            Entry<String, Integer> entry = list.get(index);
            suggestions.suggest(1 - (entry.getValue().doubleValue() / max), entry.getKey());
        }
    }

}
