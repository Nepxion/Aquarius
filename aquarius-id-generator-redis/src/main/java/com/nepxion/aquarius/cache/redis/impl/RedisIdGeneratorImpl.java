package com.nepxion.aquarius.cache.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.cache.redis.RedisIdGenerator;

@Component("redisIdGeneratorImpl")
public class RedisIdGeneratorImpl implements RedisIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(RedisIdGeneratorImpl.class);

    @Override
    public String nextUniqueId() throws Exception {
        return null;
    }
}