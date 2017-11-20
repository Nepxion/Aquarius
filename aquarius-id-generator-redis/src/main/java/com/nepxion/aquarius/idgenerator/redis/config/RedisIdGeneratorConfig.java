package com.nepxion.aquarius.idgenerator.redis.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import com.nepxion.aquarius.common.redis.handler.RedisHandler;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class RedisIdGeneratorConfig {
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        return RedisHandler.createDefaultRedisTemplate();
    }
}