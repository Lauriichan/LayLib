package me.lauriichan.laylib.logger;

import java.util.logging.Level;

public class JavaSimpleLevel extends Level {

    private static final long serialVersionUID = -6577535128307703012L;
    
    public static final Level TRACK = new JavaSimpleLevel("TRACK", INFO.intValue());
    public static final Level DEBUG = new JavaSimpleLevel("DEBUG", WARNING.intValue());

    public JavaSimpleLevel(String name, int value) {
        super(name, value);
    }

}
