package me.lauriichan.laylib.logger;

import me.lauriichan.laylib.logger.util.StringUtil;

public abstract class AbstractSimpleLogger implements ISimpleLogger {

    protected boolean debug;
    protected boolean track;

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public boolean isTracking() {
        return track;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void setTracking(boolean track) {
        this.track = track;
    }

    protected abstract void info(String message);

    @Override
    public void info(String message, Object... placeholders) {
        info(StringUtil.format(message, placeholders));
    }

    @Override
    public void info(String message, Throwable throwable, Object... placeholders) {
        info(StringUtil.format(message, placeholders));
        info(StringUtil.stackTraceToString(throwable));
    }

    @Override
    public void info(Throwable throwable) {
        info(StringUtil.stackTraceToString(throwable));
    }

    protected abstract void warning(String message);

    @Override
    public void warning(String message, Object... placeholders) {
        warning(StringUtil.format(message, placeholders));
    }

    @Override
    public void warning(String message, Throwable throwable, Object... placeholders) {
        warning(StringUtil.format(message, placeholders));
        warning(StringUtil.stackTraceToString(throwable));
    }

    @Override
    public void warning(Throwable throwable) {
        warning(StringUtil.stackTraceToString(throwable));
    }

    protected abstract void error(String message);

    @Override
    public void error(String message, Object... placeholders) {
        error(StringUtil.format(message, placeholders));
    }

    @Override
    public void error(String message, Throwable throwable, Object... placeholders) {
        error(StringUtil.format(message, placeholders));
        error(StringUtil.stackTraceToString(throwable));
    }

    @Override
    public void error(Throwable throwable) {
        error(StringUtil.stackTraceToString(throwable));
    }

    protected abstract void track(String message);

    @Override
    public void track(String message, Object... placeholders) {
        if (!track) {
            return;
        }
        track(StringUtil.format(message, placeholders));
    }

    @Override
    public void track(String message, Throwable throwable, Object... placeholders) {
        if (!track) {
            return;
        }
        track(StringUtil.format(message, placeholders));
        track(StringUtil.stackTraceToString(throwable));
    }

    @Override
    public void track(Throwable throwable) {
        if (!track) {
            return;
        }
        track(StringUtil.stackTraceToString(throwable));
    }

    protected abstract void debug(String message);

    @Override
    public void debug(String message, Object... placeholders) {
        if (!debug) {
            return;
        }
        debug(StringUtil.format(message, placeholders));
    }

    @Override
    public void debug(String message, Throwable throwable, Object... placeholders) {
        if (!debug) {
            return;
        }
        debug(StringUtil.format(message, placeholders));
        debug(StringUtil.stackTraceToString(throwable));
    }

    @Override
    public void debug(Throwable throwable) {
        if (!debug) {
            return;
        }
        debug(StringUtil.stackTraceToString(throwable));
    }

}
