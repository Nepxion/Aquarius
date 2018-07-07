package com.nepxion.aquarius.common.redis.adapter;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.aquarius.common.redis.handler.RedisHandler;

public interface RedisAdapter {
    RedisHandler getRedisHandler();
}