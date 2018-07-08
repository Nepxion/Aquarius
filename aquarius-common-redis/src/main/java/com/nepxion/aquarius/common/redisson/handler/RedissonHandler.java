package com.nepxion.aquarius.common.redisson.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.redisson.api.RedissonClient;

public interface RedissonHandler {
    // 关闭Redisson客户端连接
    void close();

    // 获取Redisson客户端是否初始化
    boolean isInitialized();

    // 获取Redisson客户端连接是否正常
    boolean isStarted();

    // 检查Redisson是否是启动状态
    void validateStartedStatus();

    // 检查Redisson是否是关闭状态
    void validateClosedStatus();

    // 获取Redisson客户端
    RedissonClient getRedisson();
}