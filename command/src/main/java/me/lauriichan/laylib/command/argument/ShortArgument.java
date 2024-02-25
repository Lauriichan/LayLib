package me.lauriichan.laylib.command.argument;

import me.lauriichan.laylib.command.IArgumentMap;

public final class ShortArgument extends NumberArgument<Short> {

    public ShortArgument(IArgumentMap argument) {
        super(argument.get("minimum", Short.class).orElse(Short.MIN_VALUE), argument.get("maximum", Short.class).orElse(Short.MAX_VALUE),
            argument.get("step", Short.class).orElse(SHORT_STEP));
    }

    @Override
    protected Short read(String input, IArgumentMap map) throws IllegalArgumentException {
        try {
            return Short.valueOf(input);
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException(exp.getMessage());
        }
    }

    @Override
    protected int compare(Short var1, Short var2) {
        return var1.compareTo(var2);
    }

    @Override
    protected Short subtract(Short var1, Short var2) {
        return (short) (var1.shortValue() - var2.shortValue());
    }

    @Override
    protected Short add(Short var1, Number var2) {
        return (short) (var1.shortValue() + var2.shortValue());
    }

}
