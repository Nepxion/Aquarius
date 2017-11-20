package com.nepxion.aquarius.common.redis.handler;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.nepxion.aquarius.common.redis.constant.RedisConstant;

public class RedisHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RedisHandler.class);

    // 创建默认RedisTemplate
    public static RedisTemplate<String, Object> createDefaultRedisTemplate() {
        try {
            ApplicationContext applicationContext = createApplicationContext(RedisConstant.CONFIG_FILE);

            return RedisHandler.createRedisTemplate(applicationContext);
        } catch (Exception e) {
            LOG.error("Initialize Redis failed", e);
        }

        return null;
    }

    // 创建ApplicationContext
    public static ApplicationContext createApplicationContext(String configPath) {
        LOG.info("Start to initialize application context with {}...", RedisConstant.CONFIG_FILE);

        String path = null;
        if (RedisHandler.class.getClassLoader().getResourceAsStream(configPath) != null) {
            path = "classpath*:" + configPath;
        } else {
            path = "file:" + configPath;
        }

        return new ClassPathXmlApplicationContext(path);
    }

    // 创建RedisTemplate
    @SuppressWarnings({ "unchecked" })
    public static RedisTemplate<String, Object> createRedisTemplate(ApplicationContext applicationContext) {
        LOG.info("Start to initialize Redis...");

        return (RedisTemplate<String, Object>) applicationContext.getBean("aquariusRedisTemplate");
    }
}