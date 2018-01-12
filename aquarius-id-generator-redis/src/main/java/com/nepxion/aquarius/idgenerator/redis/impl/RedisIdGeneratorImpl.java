package com.nepxion.aquarius.idgenerator.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.util.DateUtil;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.common.util.StringUtil;
import com.nepxion.aquarius.idgenerator.redis.RedisIdGenerator;

@Component("redisIdGeneratorImpl")
public class RedisIdGeneratorImpl implements RedisIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(RedisIdGeneratorImpl.class);

    private static final String DATE_FORMAT = "yyyyMMddHHmmssSSS";
    private static final String DECIMAL_FORMAT = "00000000";
    private static final int MAX_BATCH_COUNT = 1000;

    @Autowired
    private RedisHandler redisHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    private RedisScript<List<Object>> redisScript;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostConstruct
    public void initialize() {
        String luaScript = buildLuaScript();
        redisScript = new DefaultRedisScript(luaScript, List.class);
    }

    private String buildLuaScript() {
        StringBuilder lua = new StringBuilder();
        lua.append("local incrKey = KEYS[1];");
        lua.append("\nlocal step = ARGV[1];");
        lua.append("\nlocal count;");
        lua.append("\ncount = tonumber(redis.call('incrby', incrKey, step));");
        lua.append("\nlocal now = redis.call('time');");
        lua.append("\nreturn {now[1], now[2], count}");

        return lua.toString();
    }

    @Override
    public String nextUniqueId(String name, String key, int step, int length) {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Key is null or empty");
        }

        String compositeKey = KeyUtil.getCompositeKey(prefix, name, key);

        return nextUniqueId(compositeKey, step, length);
    }

    @Override
    public String nextUniqueId(String compositeKey, int step, int length) {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new AquariusException("Composite key is null or empty");
        }

        List<String> keys = new ArrayList<String>();
        keys.add(compositeKey);

        RedisTemplate<String, Object> redisTemplate = redisHandler.getRedisTemplate();
        List<Object> result = redisTemplate.execute(redisScript, keys, step);

        Object value1 = result.get(0);
        Object value2 = result.get(1);
        Object value3 = result.get(2);

        Date date = new Date(Long.parseLong(String.valueOf(value1)) * 1000 + Long.parseLong(String.valueOf(value2)) / 1000);

        StringBuilder builder = new StringBuilder();
        builder.append(DateUtil.formatDate(date, DATE_FORMAT));
        builder.append(StringUtil.formatString((long) value3, length, DECIMAL_FORMAT));

        String nextUniqueId = builder.toString();

        if (frequentLogPrint) {
            LOG.info("Next unique id is {} for key={}", nextUniqueId, compositeKey);
        }

        return nextUniqueId;
    }

    @Override
    public String[] nextUniqueIds(String name, String key, int step, int length, int count) {
        if (count <= 0 || count > MAX_BATCH_COUNT) {
            throw new AquariusException(String.format("Count can't be greater than %d or less than 0", MAX_BATCH_COUNT));
        }

        String[] nextUniqueIds = new String[count];
        for (int i = 0; i < count; i++) {
            nextUniqueIds[i] = nextUniqueId(name, key, step, length);
        }

        return nextUniqueIds;
    }

    @Override
    public String[] nextUniqueIds(String compositeKey, int step, int length, int count) {
        if (count <= 0 || count > MAX_BATCH_COUNT) {
            throw new AquariusException(String.format("Count can't be greater than %d or less than 0", MAX_BATCH_COUNT));
        }

        String[] nextUniqueIds = new String[count];
        for (int i = 0; i < count; i++) {
            nextUniqueIds[i] = nextUniqueId(compositeKey, step, length);
        }

        return nextUniqueIds;
    }
}