package com.nepxion.aquarius.common.curator.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public interface CuratorHandler {
    // 启动ZooKeeper客户端
    public void start() throws Exception;

    // 启动ZooKeeper客户端，直到第一次连接成功
    public void startAndBlock() throws Exception;

    // 启动ZooKeeper客户端，直到第一次连接成功，为每一次连接配置超时
    public void startAndBlock(int maxWaitTime, TimeUnit units) throws Exception;

    // 关闭ZooKeeper客户端连接
    public void close() throws Exception;

    // 获取ZooKeeper客户端是否初始化
    public boolean isInitialized();

    // 获取ZooKeeper客户端连接是否正常
    public boolean isStarted();

    // 检查ZooKeeper是否是启动状态
    public void validateStartedStatus() throws Exception;

    // 检查ZooKeeper是否是关闭状态
    public void validateClosedStatus() throws Exception;

    // 获取ZooKeeper客户端
    public CuratorFramework getCurator();

    // 判断路径是否存在
    public boolean pathExist(String path) throws Exception;

    // 判断stat是否存在
    public Stat getPathStat(String path) throws Exception;

    // 创建路径
    public void createPath(String path) throws Exception;

    // 创建路径，并写入数据
    public void createPath(String path, byte[] data) throws Exception;

    // 创建路径
    public void createPath(String path, CreateMode mode) throws Exception;

    // 创建路径，并写入数据
    public void createPath(String path, byte[] data, CreateMode mode) throws Exception;

    // 删除路径
    public void deletePath(String path) throws Exception;

    // 获取子节点名称列表
    public List<String> getChildNameList(String path) throws Exception;

    // 获取子节点路径列表
    public List<String> getChildPathList(String path) throws Exception;

    // 组装根节点路径
    public String getRootPath(String prefix);

    // 组装节点路径
    public String getPath(String prefix, String key);
}