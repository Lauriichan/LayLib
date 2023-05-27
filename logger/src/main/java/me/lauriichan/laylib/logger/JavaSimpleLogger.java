package me.lauriichan.laylib.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class JavaSimpleLogger extends AbstractSimpleLogger {

    private final Logger logger;

    public JavaSimpleLogger(final Logger logger) {
        this.logger = logger;
    }
    
    public Logger getHandle() {
        return logger;
    }

    @Override
    protected void info(String message) {
        logger.log(Level.INFO, message);
    }

    @Override
    protected void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    @Override
    protected void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    protected void track(String message) {
        logger.log(JavaSimpleLevel.TRACK, message);
    }

    @Override
    protected void debug(String message) {
        logger.log(JavaSimpleLevel.DEBUG, message);
    }

}
