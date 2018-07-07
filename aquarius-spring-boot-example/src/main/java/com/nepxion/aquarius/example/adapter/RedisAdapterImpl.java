package com.nepxion.aquarius.example.adapter;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.ApplicationContext;

import com.nepxion.aquarius.common.redis.adapter.RedisAdapter;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.redis.handler.RedisHandlerImpl;

public class RedisAdapterImpl implements RedisAdapter {
    @Override
    public RedisHandler getRedisHandler() {
        // 来自远程配置中心的内容，读取后转化成ApplicationContext
        ApplicationContext applicationContext = null;

        return new RedisHandlerImpl(applicationContext);
    }
}