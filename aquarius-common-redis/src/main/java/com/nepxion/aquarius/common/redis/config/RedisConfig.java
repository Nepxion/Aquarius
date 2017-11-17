package com.nepxion.aquarius.common.redis.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.nepxion.aquarius.common.redis.constant.RedisConstant;

@Configuration
@ImportResource(locations = { "classpath*:" + RedisConstant.CONFIG_FILE })
// @ImportResource(locations = { "classpath*:" + RedisConstant.CONFIG_FILE, "file:" + RedisConstant.CONFIG_FILE })
public class RedisConfig {

}