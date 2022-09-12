package me.lauriichan.laylib.command;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public final class Suggestions {

    private final ConcurrentHashMap<String, Double> suggestion = new ConcurrentHashMap<>();

    public void suggest(double score, Object value) {
        if (value == null) {
            return;
        }
        String stringified = value.toString();
        if (suggestion.containsKey(stringified)) {
            return;
        }
        suggestion.put(stringified, score);
    }

    public void unsuggest(Object value) {
        if (value == null) {
            return;
        }
        suggestion.remove(value.toString());
    }
    
    public boolean hasSuggestions() {
        return !suggestion.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public Entry<String, Double>[] getSuggestions(int depth) {
        return suggestion.entrySet().stream().sorted(SuggestionSorter.INSTANCE).limit(Math.max(depth, 1))
            .toArray(Entry[]::new);
    }

    private static final class SuggestionSorter implements Comparator<Entry<String, Double>> {

        private static final SuggestionSorter INSTANCE = new SuggestionSorter();

        @Override
        public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
            return -Double.compare(o1.getValue(), o2.getValue());
        }

    }

}
