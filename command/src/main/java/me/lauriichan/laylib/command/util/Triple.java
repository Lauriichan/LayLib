package me.lauriichan.laylib.command.util;

public final class Triple<A, B, C> {

    public static <A, B, C> Triple<A, B, C> of(A var1, B var2, C var3) {
        return new Triple<>(var1, var2, var3);
    }

    private final A var1;
    private final B var2;
    private final C var3;

    public Triple(A var1, B var2, C var3) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
    }

    public A getA() {
        return var1;
    }

    public B getB() {
        return var2;
    }

    public C getC() {
        return var3;
    }

}
