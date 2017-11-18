package com.nepxion.aquarius.limit.redis.config;

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
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.limit.delegate.LimitDelegate;
import com.nepxion.aquarius.limit.redis.condition.RedisLimitCondition;
import com.nepxion.aquarius.limit.redis.constant.RedisLimitConstant;
import com.nepxion.aquarius.limit.redis.delegate.RedisLimitDelegate;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class, com.nepxion.aquarius.common.redis.config.RedisConfig.class })
public class RedisLimitConfig {
    @Bean(name = RedisLimitConstant.DELEGATE_VALUE)
    @Conditional(RedisLimitCondition.class)
    public LimitDelegate redisLimitDelegate() {
        return new RedisLimitDelegate();
    }
}