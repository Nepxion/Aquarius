package com.nepxion.aquarius.idgenerator.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.DateUtil;
import com.nepxion.aquarius.common.util.StringUtil;
import com.nepxion.aquarius.idgenerator.redis.RedisIdGenerator;

@Component("redisIdGeneratorImpl")
public class RedisIdGeneratorImpl implements RedisIdGenerator {
    @Autowired
    @Qualifier("aquariusRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public String nextUniqueId(String name, String key, int step, int length) {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Key is null or empty");
        }

        List<String> keys = new ArrayList<String>();
        String spelKey = getSpelKey(name, key);
        keys.add(spelKey);

        String luaScript = buildLuaScript();

        RedisScript<List<Object>> redisScript = new DefaultRedisScript(luaScript, List.class);
        List result = redisTemplate.execute(redisScript, keys, step);

        Object value1 = result.get(0);
        Object value2 = result.get(1);
        Object value3 = result.get(2);

        Date date = new Date(Long.parseLong(String.valueOf(value1)) * 1000 + Long.parseLong(String.valueOf(value2)) / 1000);

        StringBuilder builder = new StringBuilder();
        builder.append(DateUtil.formatDate(date, DateUtil.DATE_FMT_YMDHMSSSSS));
        builder.append(StringUtil.subString((long) value3, length));

        return builder.toString();
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

    private String getSpelKey(String name, String key) {
        return prefix + "_" + name + "_" + key;
    }
}