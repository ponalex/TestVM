package org.example.auxiliary;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        String message;
        String additionInformation = record.getLevel().toString();
        String milliseconds = Long.toString(record.getMillis());
        message ="\u001B[37m" + record.getMessage() + "\n";
        return message;
    }
}
