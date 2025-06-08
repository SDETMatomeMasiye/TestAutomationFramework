package testauto.com.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LogUtil {

    private static final Map<Class<?>, Logger> cache = new ConcurrentHashMap<>();

    private static Logger getLogger(Class<?> callerClass){
        Objects.requireNonNull(callerClass,"callerClass cannot be null.");
        return cache.computeIfAbsent(callerClass, LoggerFactory::getLogger);
    }

    public static void info(String message,Class<?> callerClass){
        getLogger(callerClass).info(message);
    }

    public static void error(String message, Class<?> callerClass){
        getLogger(callerClass).error(message);
    }

    public static <T extends Throwable> void error(String message, Class<?> callerClass, T throwable){
        getLogger(callerClass).error(message,throwable);
    }

    public static <T extends Throwable> void logAndRethrow(String message, Class<?> callerClass, T throwable) throws T {
        getLogger(callerClass).error(message, throwable);
        throw throwable;
    }

    public static void debug(String message,Class<?> callerClass){
        getLogger(callerClass).debug(message);
    }

    public static void warn(String message,Class<?> callerClass){
        getLogger(callerClass).warn(message);
    }

}
