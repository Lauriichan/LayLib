package me.lauriichan.laylib.command.argument;

import me.lauriichan.laylib.command.IArgumentMap;

public final class IntegerArgument extends NumberArgument<Integer> {

    public IntegerArgument(IArgumentMap argument) {
        super(argument.get("minimum", Integer.class).orElse(Integer.MIN_VALUE),
            argument.get("maximum", Integer.class).orElse(Integer.MAX_VALUE), argument.get("step", Integer.class).orElse(INTEGER_STEP));
    }

    @Override
    protected Integer read(String input, IArgumentMap map) throws IllegalArgumentException {
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException(exp.getMessage());
        }
    }

    @Override
    protected int compare(Integer var1, Integer var2) {
        return var1.compareTo(var2);
    }

    @Override
    protected Integer subtract(Integer var1, Integer var2) {
        return var1.intValue() - var2.intValue();
    }

    @Override
    protected Integer add(Integer var1, Number var2) {
        return var1.intValue() + var2.intValue();
    }

}
