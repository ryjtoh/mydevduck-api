package com.mydevduck.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    public static LocalDateTime parse(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DEFAULT_FORMATTER) : null;
    }

}
