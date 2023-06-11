package me.lauriichan.laylib.command.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Param {

    public static final int TYPE_STRING = 0;
    public static final int TYPE_BYTE = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;
    public static final int TYPE_CLASS = 7;
    public static final int TYPE_STRING_ARRAY = 8;
    public static final int TYPE_BYTE_ARRAY = 9;
    public static final int TYPE_SHORT_ARRAY = 10;
    public static final int TYPE_INT_ARRAY = 11;
    public static final int TYPE_LONG_ARRAY = 12;
    public static final int TYPE_FLOAT_ARRAY = 13;
    public static final int TYPE_DOUBLE_ARRAY = 14;
    public static final int TYPE_CLASS_ARRAY = 15;

    String name();

    int type();

    String stringValue() default "";

    byte byteValue() default 0;

    short shortValue() default 0;

    int intValue() default 0;

    long longValue() default 0;

    float floatValue() default 0;

    double doubleValue() default 0;

    Class<?> classValue() default Void.class;

    String[] stringArrayValue() default {};

    byte[] byteArrayValue() default {};

    short[] shortArrayValue() default {};

    int[] intArrayValue() default {};

    long[] longArrayValue() default {};

    float[] floatArrayValue() default {};

    double[] doubleArrayValue() default {};

    Class<?>[] classArrayValue() default {};

}
