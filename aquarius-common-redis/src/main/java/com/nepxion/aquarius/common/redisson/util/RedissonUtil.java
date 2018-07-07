package com.nepxion.aquarius.common.redisson.util;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.io.IOException;

import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.property.AquariusContent;

public class RedissonUtil {
    private static final Logger LOG = LoggerFactory.getLogger(RedissonUtil.class);

    // 创建Yaml格式的配置文件
    public static Config createYamlFileConfig(String yamlConfigPath) throws IOException {
        LOG.info("Start to read {}...", yamlConfigPath);

        AquariusContent content = new AquariusContent(yamlConfigPath, AquariusConstant.ENCODING_UTF_8);

        return createYamlConfig(content.getContent());
    }

    // 创建Json格式的配置文件
    public static Config createJsonFileConfig(String jsonConfigPath) throws IOException {
        LOG.info("Start to read {}...", jsonConfigPath);

        AquariusContent content = new AquariusContent(jsonConfigPath, AquariusConstant.ENCODING_UTF_8);

        return createJsonConfig(content.getContent());
    }

    // 创建Yaml格式的配置文件
    public static Config createYamlConfig(String yamlConfigContent) throws IOException {
        return Config.fromYAML(yamlConfigContent);
    }

    // 创建Json格式的配置文件
    public static Config createJsonConfig(String jsonConfigContent) throws IOException {
        return Config.fromJSON(jsonConfigContent);
    }
}