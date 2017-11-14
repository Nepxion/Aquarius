package com.nepxion.aquarius.cache.redis.delegate;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.cache.delegate.CacheDelegate;
import com.nepxion.aquarius.cache.redis.entity.RedisCacheEntity;
import com.nepxion.aquarius.common.redis.constant.RedisConstant;

@Component("RedisCacheDelegate")
public class RedisCacheDelegate implements CacheDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheDelegate.class);

    private RedisTemplate<String, Object> redisTemplate;
    private RedisCacheEntity redisCacheEntity;

    @SuppressWarnings({ "unchecked", "resource" })
    @Override
    public void initialize() {
        LOG.info("Start to initialize RedisTemplate...");

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:" + RedisConstant.CONFIG_FILE);

        redisTemplate = (RedisTemplate<String, Object>) applicationContext.getBean("aquariusRedisTemplate");
        redisCacheEntity = applicationContext.getBean(RedisCacheEntity.class);
    }

    @Override
    public void destroy() {

    }

    @Override
    public String getPrefix() {
        return redisCacheEntity.getPrefix();
    }

    @Override
    public Object invokeCacheable(MethodInvocation invocation, String value, String key, long expire) throws Throwable {
        System.out.println("1");
        return null;
    }

    @Override
    public Object invokeCacheEvict(MethodInvocation invocation, String value, String key, boolean allEntries, boolean beforeInvocation) throws Throwable {
        System.out.println("2");
        return null;
    }

    @Override
    public Object invokeCachePut(MethodInvocation invocation, String value, String key, long expire) throws Throwable {
        System.out.println("3");
        return null;
    }
}