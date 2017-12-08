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

import java.text.DecimalFormat;

import org.apache.commons.lang3.ArrayUtils;

public class StringUtil {
    public static DecimalFormat format = new DecimalFormat("00000000");

    public static String subString(long key, int length) {
        String value = String.valueOf(key);
        if (value.length() < length) {
            return format.format(key);
        } else {
            return value.substring(value.length() - length, value.length());
        }
    }

    public static String convert(String[] arrays) {
        if (ArrayUtils.isEmpty(arrays)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (String array : arrays) {
            builder.append(array).append(",");
        }

        String result = builder.toString();
        result = result.substring(0, result.length() - 1);

        return result;
    }
}