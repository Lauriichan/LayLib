package me.lauriichan.laylib.command.argument;

import me.lauriichan.laylib.command.IArgumentMap;

public final class DoubleArgument extends NumberArgument<Double> {

    public DoubleArgument(IArgumentMap argument) {
        super(argument.get("minimum", Double.class).orElse(Double.MIN_VALUE), argument.get("maximum", Double.class).orElse(Double.MAX_VALUE),
            argument.get("step", Double.class).orElse(DOUBLE_STEP));
    }

    @Override
    protected Double read(String input) throws IllegalArgumentException {
        try {
            return Double.valueOf(input);
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException(exp.getMessage());
        }
    }

    @Override
    protected int compare(Double var1, Double var2) {
        return var1.compareTo(var2);
    }

    @Override
    protected Double subtract(Double var1, Double var2) {
        return var1.doubleValue() - var2.doubleValue();
    }

    @Override
    protected Double add(Double var1, Number var2) {
        return var1.doubleValue() + var2.doubleValue();
    }

}
