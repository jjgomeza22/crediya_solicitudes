package co.com.crediya.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final String APP_NAME = "crediya_solicitudes";
    private static final Logger logger = LoggerFactory.getLogger(Log.class);

    private Log() {
        //Empty constructor
    }

    public static void logInfo(String event, String source, String message) {
        logger.info("APPLICATION= {} SOURCE= {} EVENT= {} MESSAGE= {}", APP_NAME, source, event, message);
    }

    public static void logError(String event, String source, String message, Exception ex) {
        logger.error("APPLICATION= {} SOURCE= {} EVENT= {} MESSAGE= {}", APP_NAME, source, event, message, ex);
    }
}
