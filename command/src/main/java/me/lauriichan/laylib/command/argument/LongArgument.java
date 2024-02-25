package me.lauriichan.laylib.command.argument;

import me.lauriichan.laylib.command.IArgumentMap;

public final class LongArgument extends NumberArgument<Long> {

    public LongArgument(IArgumentMap argument) {
        super(argument.get("minimum", Long.class).orElse(Long.MIN_VALUE), argument.get("maximum", Long.class).orElse(Long.MAX_VALUE),
            argument.get("step", Long.class).orElse(LONG_STEP));
    }

    @Override
    protected Long read(String input, IArgumentMap map) throws IllegalArgumentException {
        try {
            return Long.valueOf(input);
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException(exp.getMessage());
        }
    }

    @Override
    protected int compare(Long var1, Long var2) {
        return var1.compareTo(var2);
    }

    @Override
    protected Long subtract(Long var1, Long var2) {
        return var1.longValue() - var2.longValue();
    }

    @Override
    protected Long add(Long var1, Number var2) {
        return var1.longValue() + var2.longValue();
    }

}
