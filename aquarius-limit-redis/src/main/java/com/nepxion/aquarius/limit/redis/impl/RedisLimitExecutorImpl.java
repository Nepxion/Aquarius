package com.nepxion.aquarius.limit.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.ArrayList;
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

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.limit.LimitExecutor;

public class RedisLimitExecutorImpl implements LimitExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLimitExecutorImpl.class);

    @Autowired
    private RedisHandler redisHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + ":false}")
    private Boolean frequentLogPrint;

    private RedisScript<Number> redisScript;

    @PostConstruct
    public void initialize() {
        String luaScript = buildLuaScript();
        redisScript = new DefaultRedisScript<Number>(luaScript, Number.class);
    }

    private String buildLuaScript() {
        StringBuilder lua = new StringBuilder();
        lua.append("local c");
        lua.append("\nc = redis.call('get',KEYS[1])");
        lua.append("\nif c and tonumber(c) > tonumber(ARGV[1]) then"); // 调用不超过最大值，则直接返回
        lua.append("\nreturn c;");
        lua.append("\nend");
        lua.append("\nc = redis.call('incr',KEYS[1])"); // 执行计算器自加
        lua.append("\nif tonumber(c) == 1 then");
        lua.append("\nredis.call('expire',KEYS[1],ARGV[2])"); // 从第一次调用开始限流，设置对应键值的过期
        lua.append("\nend");
        lua.append("\nreturn c;");

        return lua.toString();
    }

    @Override
    public boolean tryAccess(String name, String key, int limitPeriod, int limitCount) throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Key is null or empty");
        }

        String compositeKey = KeyUtil.getCompositeKey(prefix, name, key);

        return tryAccess(compositeKey, limitPeriod, limitCount);
    }

    @Override
    public boolean tryAccess(String compositeKey, int limitPeriod, int limitCount) throws Exception {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new AquariusException("Composite key is null or empty");
        }

        List<String> keys = new ArrayList<String>();
        keys.add(compositeKey);

        RedisTemplate<String, Object> redisTemplate = redisHandler.getRedisTemplate();
        Number count = redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);

        if (frequentLogPrint) {
            LOG.info("Access try count is {} for key={}", count, compositeKey);
        }

        return count.intValue() <= limitCount;
    }
}