package com.nepxion.aquarius.common.property;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

import com.nepxion.aquarius.common.util.MathsUtil;

public class AquariusProperties implements Serializable {
    private static final long serialVersionUID = 1722927234615067236L;

    private final Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    public AquariusProperties(String path, String encoding) throws IOException {
        this(new StringBuilder(new AquariusContent(path, encoding).getContent()), encoding);
    }

    public AquariusProperties(StringBuilder stringBuilder, String encoding) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(stringBuilder.toString(), encoding);
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Iterator<Object> iterator = properties.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next().toString();
                String value = properties.getProperty(key);
                put(key, value);
            }
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) map.get(key);
    }

    public void put(String key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null for key=" + key);
        }

        Long result = MathsUtil.calculate(value.toString());
        if (result != null) {
            map.put(key, result);
        } else {
            map.put(key, value);
        }
    }

    public String getString(String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Value is null for key=" + key);
        }

        return String.valueOf(value);
    }

    public int getInteger(String key) {
        String value = getString(key);

        return Integer.parseInt(value);
    }

    public long getLong(String key) {
        String value = getString(key);

        return Long.parseLong(value);
    }

    public boolean getBoolean(String key) {
        String value = getString(key);

        return Boolean.valueOf(value);
    }

    public void putString(String key, String value) {
        map.put(key, value);
    }

    public void putInteger(String key, String value) {
        map.put(key, Integer.parseInt(value));
    }

    public void putLong(String key, String value) {
        map.put(key, Long.parseLong(value));
    }

    public void putBoolean(String key, String value) {
        map.put(key, Boolean.valueOf(value));
    }

    public void mergeProperties(AquariusProperties properties) {
        map.putAll(properties.getMap());
    }

    @Override
    public String toString() {
        return map.toString();
    }
}