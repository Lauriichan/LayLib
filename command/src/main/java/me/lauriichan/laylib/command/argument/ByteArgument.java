package me.lauriichan.laylib.command.argument;

import me.lauriichan.laylib.command.IArgumentMap;

public final class ByteArgument extends NumberArgument<Byte> {

    public ByteArgument(IArgumentMap argument) {
        super(argument.get("minimum", Byte.class).orElse(Byte.MIN_VALUE), argument.get("maximum", Byte.class).orElse(Byte.MAX_VALUE),
            argument.get("step", Byte.class).orElse(BYTE_STEP));
    }

    @Override
    protected Byte read(String input, IArgumentMap map) throws IllegalArgumentException {
        try {
            return Byte.valueOf(input);
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException(exp.getMessage());
        }
    }

    @Override
    protected int compare(Byte var1, Byte var2) {
        return var1.compareTo(var2);
    }

    @Override
    protected Byte subtract(Byte var1, Byte var2) {
        return (byte) (var1.byteValue() - var2.byteValue());
    }

    @Override
    protected Byte add(Byte var1, Number var2) {
        return (byte) (var1.byteValue() + var2.byteValue());
    }

}
