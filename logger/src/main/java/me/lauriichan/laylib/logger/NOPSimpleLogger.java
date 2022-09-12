package me.lauriichan.laylib.logger;

final class NOPSimpleLogger implements ISimpleLogger {

    final static NOPSimpleLogger INSTANCE = new NOPSimpleLogger();

    private NOPSimpleLogger() {}

    @Override
    public boolean isDebug() {
        return false;
    }

    @Override
    public boolean isTracking() {
        return false;
    }

    @Override
    public void setDebug(boolean debug) {}

    @Override
    public void setTracking(boolean tracking) {}

    @Override
    public void info(String message, Object... placeholders) {}

    @Override
    public void info(String message, Throwable throwable, Object... placeholders) {}

    @Override
    public void info(Throwable throwable) {}

    @Override
    public void track(String message, Object... placeholders) {}

    @Override
    public void track(String message, Throwable throwable, Object... placeholders) {}

    @Override
    public void track(Throwable throwable) {}

    @Override
    public void warning(String message, Object... placeholders) {}

    @Override
    public void warning(String message, Throwable throwable, Object... placeholders) {}

    @Override
    public void warning(Throwable throwable) {}

    @Override
    public void error(String message, Object... placeholders) {}

    @Override
    public void error(String message, Throwable throwable, Object... placeholders) {}

    @Override
    public void error(Throwable throwable) {}

    @Override
    public void debug(String message, Object... placeholders) {}

    @Override
    public void debug(String message, Throwable throwable, Object... placeholders) {}

    @Override
    public void debug(Throwable throwable) {}

}