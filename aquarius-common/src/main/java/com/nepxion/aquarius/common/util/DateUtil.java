package com.nepxion.aquarius.common.util;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DateUtil {
    private static volatile Map<String, DateTimeFormatter> dateFormatMap = new ConcurrentHashMap<String, DateTimeFormatter>();

    public static String formatDate(Date date, String pattern) {
        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter(pattern);

        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);

        return localDateTime.format(dateTimeFormatter);
    }

    public static Date parseDate(String date, String pattern) {
        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter(pattern);

        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);

        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zoneId).toInstant();

        return Date.from(instant);
    }

    private static DateTimeFormatter getDateTimeFormatter(String pattern) {
        DateTimeFormatter dateTimeFormatter = dateFormatMap.get(pattern);
        if (dateTimeFormatter == null) {
            DateTimeFormatter newDateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
            dateTimeFormatter = dateFormatMap.putIfAbsent(pattern, newDateTimeFormatter);
            if (dateTimeFormatter == null) {
                dateTimeFormatter = newDateTimeFormatter;
            }
        }

        return dateTimeFormatter;
    }
}