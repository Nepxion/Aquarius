package com.nepxion.aquarius.example.adapter;

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

import com.nepxion.aquarius.common.redisson.adapter.RedissonAdapter;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandler;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandlerImpl;
import com.nepxion.aquarius.common.redisson.util.RedissonUtil;

public class RedissonAdapterImpl implements RedissonAdapter {
    @Override
    public RedissonHandler getRedissonHandler() {
        // 来自远程配置中心的内容
        String yamlConfigContent = "...";

        Config config = null;
        try {
            config = RedissonUtil.createYamlConfig(yamlConfigContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*String jsonConfigContent = "...";

        Config config = null;
        try {
            config = RedissonUtil.createJsonConfig(jsonConfigContent);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return new RedissonHandlerImpl(config);
    }
}