package com.nepxion.aquarius.idgenerator.redis.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.common.redis.configuration.RedisConfiguration;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.redis.handler.RedisHandlerImpl;
import com.nepxion.aquarius.idgenerator.redis.RedisIdGenerator;
import com.nepxion.aquarius.idgenerator.redis.impl.RedisIdGeneratorImpl;

@Configuration
@Import({ RedisConfiguration.class })
public class RedisIdGeneratorConfiguration {
    @Bean
    public RedisIdGenerator redisIdGenerator() {
        return new RedisIdGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisHandler redisHandler() {
        return new RedisHandlerImpl();
    }
}