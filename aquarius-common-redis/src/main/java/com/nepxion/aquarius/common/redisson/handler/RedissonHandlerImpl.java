package com.nepxion.aquarius.common.redisson.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.redisson.exception.RedissonException;
import com.nepxion.aquarius.common.redisson.util.RedissonUtil;

public class RedissonHandlerImpl implements RedissonHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RedissonHandlerImpl.class);

    private RedissonClient redisson;

    public RedissonHandlerImpl(String yamlConfigPath) {
        try {
            Config config = RedissonUtil.createYamlFileConfig(yamlConfigPath);

            initialize(config);
        } catch (Exception e) {
            LOG.error("Initialize Redisson failed", e);
        }
    }

    public RedissonHandlerImpl(Config config) {
        try {
            initialize(config);
        } catch (Exception e) {
            LOG.error("Initialize Redisson failed", e);
        }
    }

    // 创建Redisson
    private void initialize(Config config) {
        create(config);
    }

    // 使用config创建Redisson
    private void create(Config config) {
        LOG.info("Start to initialize Redisson...");

        if (redisson != null) {
            throw new RedissonException("Redisson isn't null, it has been initialized already");
        }

        redisson = Redisson.create(config);
    }

    // 关闭Redisson客户端连接
    @Override
    public void close() {
        LOG.info("Start to close Redisson...");

        validateStartedStatus();

        redisson.shutdown();
    }

    // 获取Redisson客户端是否初始化
    @Override
    public boolean isInitialized() {
        return redisson != null;
    }

    // 获取Redisson客户端连接是否正常
    @Override
    public boolean isStarted() {
        if (redisson == null) {
            throw new RedissonException("Redisson isn't initialized");
        }

        return !redisson.isShutdown() && !redisson.isShuttingDown();
    }

    // 检查Redisson是否是启动状态
    @Override
    public void validateStartedStatus() {
        if (redisson == null) {
            throw new RedissonException("Redisson isn't initialized");
        }

        if (!isStarted()) {
            throw new RedissonException("Redisson is closed");
        }
    }

    // 检查Redisson是否是关闭状态
    @Override
    public void validateClosedStatus() {
        if (redisson == null) {
            throw new RedissonException("Redisson isn't initialized");
        }

        if (isStarted()) {
            throw new RedissonException("Redisson is started");
        }
    }

    // 获取Redisson客户端
    @Override
    public RedissonClient getRedisson() {
        return redisson;
    }
}