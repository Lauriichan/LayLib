package me.lauriichan.laylib.command.argument;

import java.util.Objects;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IArgumentType;
import me.lauriichan.laylib.command.Suggestions;

public abstract class NumberArgument<E extends Number> implements IArgumentType<E> {

    public static final byte BYTE_STEP = 1;
    public static final short SHORT_STEP = 1;
    public static final int INTEGER_STEP = 1;
    public static final long LONG_STEP = 1;
    public static final float FLOAT_STEP = 1;
    public static final double DOUBLE_STEP = 1;

    public static final int SUGGESTIONS = 5;
    private static final double DIV_SUGGESTIONS = SUGGESTIONS;
    private static final double NORM_SUGGESTIONS = Math.ceil(SUGGESTIONS / 2d);

    protected final E minimum;
    protected final E maximum;
    protected final E step;

    public NumberArgument(E minimum, E maximum, E step) {
        this.minimum = Objects.requireNonNull(minimum, "Minimum can't be null");
        this.maximum = Objects.requireNonNull(maximum, "Maximum can't be null");
        this.step = Objects.requireNonNull(step, "Step can't be null");
    }

    @Override
    public E parse(Actor<?> actor, String input) throws IllegalArgumentException {
        E value = read(input.trim());
        if (compare(value, minimum) < 0) {
            throw new IllegalArgumentException("Value lower than " + minimum.toString());
        }
        if (compare(value, maximum) > 0) {
            throw new IllegalArgumentException("Value higher than " + maximum.toString());
        }
        return value;
    }

    @Override
    public void suggest(Actor<?> actor, String input, Suggestions suggestions) {
        E value;
        try {
            value = read(input);
        } catch (IllegalArgumentException exp) {
            suggestions.suggest(0.6d, maximum);
            suggestions.suggest(0.4d, minimum);
            return;
        }
        if (compare(value, maximum) > 0) {
            E current = maximum;
            for (int index = 0; index < SUGGESTIONS; index++) {
                suggestions.suggest((1 - (index / DIV_SUGGESTIONS)) / NORM_SUGGESTIONS, current);
                current = subtract(current, step);
            }
            return;
        }
        if (compare(value, minimum) >= 0) {
            return;
        }
        E current = minimum;
        for (int index = 0; index < SUGGESTIONS; index++) {
            suggestions.suggest((1 - (index / DIV_SUGGESTIONS)) / NORM_SUGGESTIONS, current);
            current = add(current, step);
        }
    }

    protected abstract E read(String input) throws IllegalArgumentException;

    protected abstract int compare(E var1, E var2);

    protected abstract E subtract(E var1, E var2);

    protected abstract E add(E var1, Number var2);

}
