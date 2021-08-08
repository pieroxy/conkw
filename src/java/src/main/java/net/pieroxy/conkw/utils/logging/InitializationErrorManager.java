package net.pieroxy.conkw.utils.logging;

import java.util.logging.ErrorManager;

public class InitializationErrorManager  extends ErrorManager {
    Exception lastException;
    @Override
    public void error(String msg, Exception ex, int code) {
        lastException = ex;
    }
}