package com.nepxion.aquarius.common.redis.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.data.redis.core.RedisTemplate;

public interface RedisHandler {
    // 获取RedisTemplate客户端是否初始化
    public boolean isInitialized();

    // 检查RedisTemplate是否已经初始化
    public void validateInitializedStatus();

    // 获取RedisTemplate
    RedisTemplate<String, Object> getRedisTemplate();
}