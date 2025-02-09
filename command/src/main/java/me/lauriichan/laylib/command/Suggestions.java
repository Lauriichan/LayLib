package me.lauriichan.laylib.command;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import me.lauriichan.laylib.reflection.JavaLookup;

public final class Suggestions {

    private static final MethodHandle ELEMENT_DATA_ACCESS = JavaLookup.PLATFORM.findGetter(ArrayList.class, "elementData", Object[].class);

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
        ArrayList<Entry<String, Double>> list = new ArrayList<>(suggestion.entrySet());
        list.sort(SuggestionSorter.INSTANCE);
        if (depth <= 0) {
            return list.toArray(Entry[]::new);
        }
        Entry<String, Double>[] output = new Entry[Math.min(list.size(), depth)];
        try {
            System.arraycopy((Object[]) ELEMENT_DATA_ACCESS.invoke(list), 0, output, 0, output.length);
        } catch (Throwable e) {
            for (int i = 0; i < output.length; i++) {
                output[i] = list.get(i);
            }
        }
        return output;
    }

    private static final class SuggestionSorter implements Comparator<Entry<String, Double>> {

        private static final SuggestionSorter INSTANCE = new SuggestionSorter();

        @Override
        public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
            return -Double.compare(o1.getValue(), o2.getValue());
        }

    }

}
