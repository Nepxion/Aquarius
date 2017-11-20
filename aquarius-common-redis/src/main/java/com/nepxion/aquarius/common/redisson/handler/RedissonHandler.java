package com.nepxion.aquarius.common.redisson.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.property.AquariusContent;

public class RedissonHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RedissonHandler.class);

    // 创建Yaml格式的配置文件
    public static Config createYamlConfig(String yamlConfigPath) throws IOException {
        LOG.info("Start to read {}...", yamlConfigPath);

        AquariusContent content = new AquariusContent(yamlConfigPath);

        return Config.fromYAML(content.getContent());
    }

    // 创建Json格式的配置文件
    public static Config createJsonConfig(String jsonConfigPath) throws IOException {
        LOG.info("Start to read {}...", jsonConfigPath);

        AquariusContent content = new AquariusContent(jsonConfigPath);

        return Config.fromJSON(content.getContent());
    }

    // 使用config创建Redisson
    public static RedissonClient createRedisson(Config config) {
        LOG.info("Start to initialize Redisson...");

        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

    // 关闭Redisson客户端连接
    public static void closeRedisson(RedissonClient redisson) {
        LOG.info("Start to close Redisson...");

        redisson.shutdown();
    }

    // Redisson客户端连接是否正常
    public static boolean isStarted(RedissonClient redisson) {
        return !redisson.isShutdown() && !redisson.isShuttingDown();
    }
}