package me.lauriichan.laylib.logger;

public interface ISimpleLogger {

    public static final ISimpleLogger NOP = NOPSimpleLogger.INSTANCE;

    boolean isDebug();

    void setDebug(boolean debug);

    boolean isTracking();

    void setTracking(boolean tracking);

    void info(String message, Object... placeholders);

    void info(String message, Throwable throwable, Object... placeholders);

    void info(Throwable throwable);

    void track(String message, Object... placeholders);

    void track(String message, Throwable throwable, Object... placeholders);

    void track(Throwable throwable);

    void warning(String message, Object... placeholders);

    void warning(String message, Throwable throwable, Object... placeholders);

    void warning(Throwable throwable);

    void error(String message, Object... placeholders);

    void error(String message, Throwable throwable, Object... placeholders);

    void error(Throwable throwable);

    void debug(String message, Object... placeholders);

    void debug(String message, Throwable throwable, Object... placeholders);

    void debug(Throwable throwable);

}
