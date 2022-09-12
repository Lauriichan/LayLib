package me.lauriichan.laylib.command.argument;

import me.lauriichan.laylib.command.IArgumentMap;

public final class FloatArgument extends NumberArgument<Float> {

    public FloatArgument(IArgumentMap argument) {
        super(argument.get("minimum", Float.class).orElse(Float.MIN_VALUE), argument.get("maximum", Float.class).orElse(Float.MAX_VALUE),
            argument.get("step", Float.class).orElse(FLOAT_STEP));
    }

    @Override
    protected Float read(String input) throws IllegalArgumentException {
        try {
            return Float.valueOf(input);
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException(exp.getMessage());
        }
    }

    @Override
    protected int compare(Float var1, Float var2) {
        return var1.compareTo(var2);
    }

    @Override
    protected Float subtract(Float var1, Float var2) {
        return var1.floatValue() - var2.floatValue();
    }

    @Override
    protected Float add(Float var1, Number var2) {
        return var1.floatValue() + var2.floatValue();
    }

}
