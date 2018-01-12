package com.nepxion.aquarius.common.util;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

public class KeyUtil {
    public static String getCompositeKey(String prefix, String name, String key) {
        return prefix + "_" + name + "_" + key;
    }

    public static String getCompositeWildcardKey(String prefix, String name) {
        return prefix + "_" + name + "*";
    }

    public static String getCompositeWildcardKey(String key) {
        return key + "*";
    }
}