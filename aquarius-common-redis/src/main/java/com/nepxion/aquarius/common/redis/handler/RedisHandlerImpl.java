package com.nepxion.aquarius.common.redis.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisHandlerImpl implements RedisHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RedisHandlerImpl.class);

    private RedisTemplate<String, Object> redisTemplate;

    public RedisHandlerImpl(String configPath) {
        try {
            ApplicationContext applicationContext = createApplicationContext(configPath);

            initialize(applicationContext);
        } catch (Exception e) {
            LOG.error("Initialize Redis failed", e);
        }
    }

    public RedisHandlerImpl(ApplicationContext applicationContext) {
        try {
            initialize(applicationContext);
        } catch (Exception e) {
            LOG.error("Initialize Redis failed", e);
        }
    }

    // 创建Redis
    public void initialize(ApplicationContext applicationContext) {
        create(applicationContext);
    }

    // 创建ApplicationContext
    public ApplicationContext createApplicationContext(String configPath) {
        LOG.info("Start to initialize application context with {}...", configPath);

        String path = null;
        if (RedisHandlerImpl.class.getClassLoader().getResourceAsStream(configPath) != null) {
            path = "classpath*:" + configPath;
        } else {
            path = "file:" + configPath;
        }

        return new ClassPathXmlApplicationContext(path);
    }

    // 创建RedisTemplate
    @SuppressWarnings({ "unchecked" })
    public void create(ApplicationContext applicationContext) {
        LOG.info("Start to initialize Redis...");

        redisTemplate = (RedisTemplate<String, Object>) applicationContext.getBean("aquariusRedisTemplate");
    }

    // 获取RedisTemplate
    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
}