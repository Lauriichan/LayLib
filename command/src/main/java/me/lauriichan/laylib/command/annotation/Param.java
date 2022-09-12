package me.lauriichan.laylib.command.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Param {

    String name();
    
    /**
     * 0: String<br>
     * 1: byte<br>
     * 2: short<br>
     * 3: int<br>
     * 4: long<br>
     * 5: float<br>
     * 6: double<br>
     * 7: Class<br>
     * 8: String[]<br>
     * 9: byte[]<br>
     * 10: short[]<br>
     * 11: int[]<br>
     * 12: long[]<br>
     * 13: float[]<br>
     * 14: double[]<br>
     * 15: Class[]
     * 
     * @return type of this param
     */
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
