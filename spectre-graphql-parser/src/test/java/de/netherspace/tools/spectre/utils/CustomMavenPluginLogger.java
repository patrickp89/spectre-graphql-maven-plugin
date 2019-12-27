package de.netherspace.tools.spectre.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.logging.Log;

@Slf4j
public class CustomMavenPluginLogger implements Log {

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(CharSequence charSequence) {
        logger.debug(charSequence.toString());
    }

    @Override
    public void debug(CharSequence charSequence, Throwable throwable) {
        logger.debug(charSequence.toString(), throwable);
    }

    @Override
    public void debug(Throwable throwable) {
        logger.debug("", throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(CharSequence charSequence) {
        logger.info(charSequence.toString());
    }

    @Override
    public void info(CharSequence charSequence, Throwable throwable) {
        logger.info(charSequence.toString(), throwable);
    }

    @Override
    public void info(Throwable throwable) {
        logger.info("", throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(CharSequence charSequence) {
        logger.warn(charSequence.toString());
    }

    @Override
    public void warn(CharSequence charSequence, Throwable throwable) {
        logger.warn(charSequence.toString(), throwable);
    }

    @Override
    public void warn(Throwable throwable) {
        logger.warn("", throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(CharSequence charSequence) {
        logger.error(charSequence.toString());
    }

    @Override
    public void error(CharSequence charSequence, Throwable throwable) {
        logger.error(charSequence.toString(), throwable);
    }

    @Override
    public void error(Throwable throwable) {
        logger.error("", throwable);
    }
}
