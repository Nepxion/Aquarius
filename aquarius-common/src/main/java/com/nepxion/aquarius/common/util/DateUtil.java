package com.nepxion.aquarius.common.util;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final String DATE_FMT_YMDHMSSSSS = "yyyyMMddHHmmssSSS";

    private static SimpleDateFormat format = new SimpleDateFormat();

    public static String formatDate(Date date, String pattern) {
        format.applyPattern(pattern);

        return format.format(date);
    }
}