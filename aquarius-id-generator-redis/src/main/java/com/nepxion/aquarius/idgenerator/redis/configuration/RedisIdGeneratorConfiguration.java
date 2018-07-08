package com.nepxion.aquarius.idgenerator.redis.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.common.redis.adapter.RedisAdapter;
import com.nepxion.aquarius.common.redis.constant.RedisConstant;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.redis.handler.RedisHandlerImpl;
import com.nepxion.aquarius.idgenerator.redis.RedisIdGenerator;
import com.nepxion.aquarius.idgenerator.redis.impl.RedisIdGeneratorImpl;

@Configuration
public class RedisIdGeneratorConfiguration {
    @Value("${" + RedisConstant.CONFIG_PATH + ":" + RedisConstant.DEFAULT_CONFIG_PATH + "}")
    private String redisConfigPath;

    @Autowired(required = false)
    private RedisAdapter redisAdapter;

    @Bean
    public RedisIdGenerator redisIdGenerator() {
        return new RedisIdGeneratorImpl();
    }

    @Bean
    public RedisHandler redisHandler() {
        if (redisAdapter != null) {
            return redisAdapter.getRedisHandler();
        }

        return new RedisHandlerImpl(redisConfigPath);
    }
}