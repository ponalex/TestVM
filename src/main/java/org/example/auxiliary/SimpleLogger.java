package org.example.auxiliary;

import java.util.logging.*;

import static java.util.logging.Level.*;

public class SimpleLogger {

    public static Logger logger = Logger.getGlobal();

    public static Formatter formatter = new Formatter() {
        @Override
        public String format(LogRecord record) {
            String result = "";
            Level s = record.getLevel();
            switch (s.getName()) {
                case "SEVERE":
                    result = "[ERROR] " + ": " + record.getMessage() + "\n";
                    break;
                case "WARNING":
                    result = "[WARNING] " + record.getMessage() + "\n";
                    break;
                default:
                    result = "[INFO]" + record.getMessage() + "\n";
                    break;
            }
            return result;
        }
    };

    public static void setLoggerLevel(Level logLevel) {
        logger.setLevel(logLevel);
        Handler[] handlers = logger.getParent().getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(logLevel);
            handler.setFormatter(formatter);
        }
    }


    public static void printOut(String message) {
        logger.log(FINEST, message);
    }

    public static void printWarning(String message) {
        logger.log(WARNING, message);
    }

    public static void printError(String message) {
        logger.log(SEVERE, message);
    }
}
