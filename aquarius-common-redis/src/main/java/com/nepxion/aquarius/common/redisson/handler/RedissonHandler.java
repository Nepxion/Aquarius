package com.nepxion.aquarius.common.redisson.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.property.AquariusContent;
import com.nepxion.aquarius.common.redisson.constant.RedissonConstant;
import com.nepxion.aquarius.common.redisson.exception.RedissonException;

public class RedissonHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RedissonHandler.class);

    private RedissonClient redisson;

    // 创建默认Redisson
    public RedissonHandler() {
        try {
            Config config = createYamlConfig(RedissonConstant.CONFIG_FILE);

            create(config);
        } catch (Exception e) {
            LOG.error("Initialize Redisson failed", e);
        }
    }

    // 创建Yaml格式的配置文件
    public Config createYamlConfig(String yamlConfigPath) throws IOException {
        LOG.info("Start to read {}...", yamlConfigPath);

        AquariusContent content = new AquariusContent(yamlConfigPath, AquariusConstant.ENCODING_UTF_8);

        return Config.fromYAML(content.getContent());
    }

    // 创建Json格式的配置文件
    public Config createJsonConfig(String jsonConfigPath) throws IOException {
        LOG.info("Start to read {}...", jsonConfigPath);

        AquariusContent content = new AquariusContent(jsonConfigPath, AquariusConstant.ENCODING_UTF_8);

        return Config.fromJSON(content.getContent());
    }

    // 使用config创建Redisson
    public void create(Config config) throws Exception {
        LOG.info("Start to initialize Redisson...");

        if (redisson != null) {
            throw new RedissonException("Redisson isn't null, it has been initialized already");
        }

        redisson = Redisson.create(config);
    }

    // 关闭Redisson客户端连接
    public void close() throws Exception {
        LOG.info("Start to close Redisson...");

        validateStartedStatus();

        redisson.shutdown();
    }

    // 获取Redisson客户端是否初始化
    public boolean isInitialized() {
        return redisson != null;
    }

    // 获取Redisson客户端连接是否正常
    public boolean isStarted() {
        if (redisson == null) {
            throw new RedissonException("Redisson is null");
        }

        return !redisson.isShutdown() && !redisson.isShuttingDown();
    }

    // 检查Redisson是否是启动状态
    public void validateStartedStatus() throws Exception {
        if (redisson == null) {
            throw new RedissonException("Redisson is null");
        }

        if (!isStarted()) {
            throw new RedissonException("Redisson is closed");
        }
    }

    // 检查Redisson是否是关闭状态
    public void validateClosedStatus() throws Exception {
        if (redisson == null) {
            throw new RedissonException("Redisson is null");
        }

        if (isStarted()) {
            throw new RedissonException("Redisson is started");
        }
    }

    // 获取Redisson客户端
    public RedissonClient getRedisson() {
        return redisson;
    }
}