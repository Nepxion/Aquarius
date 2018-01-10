package com.nepxion.aquarius.common.curator.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.constant.CuratorConstant;
import com.nepxion.aquarius.common.curator.exception.CuratorException;
import com.nepxion.aquarius.common.property.AquariusProperties;

public class CuratorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CuratorHandler.class);

    private CuratorFramework curator;

    // 创建默认Curator，并初始化根节点
    public CuratorHandler(String prefix) {
        try {
            AquariusProperties config = createPropertyConfig(CuratorConstant.CONFIG_FILE);
            create(config);

            String rootPath = getRootPath(prefix);
            if (!pathExist(rootPath)) {
                createPath(rootPath, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            LOG.error("Initialize Curator failed", e);
        }
    }

    // 创建Property格式的配置文件
    public AquariusProperties createPropertyConfig(String propertyConfigPath) throws IOException {
        LOG.info("Start to read {}...", propertyConfigPath);

        return new AquariusProperties(propertyConfigPath, AquariusConstant.ENCODING_GBK, AquariusConstant.ENCODING_UTF_8);
    }

    // 创建Curator
    public void create(AquariusProperties properties) throws Exception {
        String retryType = properties.getString(CuratorConstant.RETRY_TYPE);
        RetryPolicy retryPolicy = null;
        if (StringUtils.equals(retryType, CuratorConstant.RETRY_TYPE_EXPONENTIAL_BACKOFF_RETRY)) {
            int baseSleepTimeMs = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_BASE_SLEEP_TIME_MS);
            int maxRetries = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_MAX_RETRIES);
            retryPolicy = createExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        } else if (StringUtils.equals(retryType, CuratorConstant.RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY)) {
            int baseSleepTimeMs = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_BASE_SLEEP_TIME_MS);
            int maxSleepTimeMs = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_MAX_SLEEP_TIME_MS);
            int maxRetries = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_MAX_RETRIES);
            retryPolicy = createBoundedExponentialBackoffRetry(baseSleepTimeMs, maxSleepTimeMs, maxRetries);
        } else if (StringUtils.equals(retryType, CuratorConstant.RETRY_TYPE_RETRY_NTIMES)) {
            int count = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_COUNT);
            int sleepMsBetweenRetries = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_SLEEP_MS_BETWEEN_RETRIES);
            retryPolicy = createRetryNTimes(count, sleepMsBetweenRetries);
        } else if (StringUtils.equals(retryType, CuratorConstant.RETRY_TYPE_RETRY_FOREVER)) {
            int retryIntervalMs = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_RETRY_INTERVAL_MS);
            retryPolicy = createRetryForever(retryIntervalMs);
        } else if (StringUtils.equals(retryType, CuratorConstant.RETRY_TYPE_RETRY_UNTIL_ELAPSED)) {
            int maxElapsedTimeMs = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_MAX_ELAPSED_TIME_MS);
            int sleepMsBetweenRetries = properties.getInteger(retryType + "-" + CuratorConstant.PARAMETER_NAME_SLEEP_MS_BETWEEN_RETRIES);
            retryPolicy = createRetryUntilElapsed(maxElapsedTimeMs, sleepMsBetweenRetries);
        } else {
            throw new CuratorException("Invalid config value for retryType=" + retryType);
        }

        String connectString = properties.getString(CuratorConstant.CONNECT_STRING);
        int sessionTimeoutMs = properties.getInteger(CuratorConstant.SESSION_TIMEOUT_MS);
        int connectionTimeoutMs = properties.getInteger(CuratorConstant.CONNECTION_TIMEOUT_MS);

        create(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);

        startAndBlock();
    }

    // 重试指定的次数, 且每一次重试之间停顿的时间逐渐增加
    public RetryPolicy createExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);

        return retryPolicy;
    }

    // 重试指定的次数, 且每一次重试之间停顿的时间逐渐增加，增加了最大重试次数的控制
    public RetryPolicy createBoundedExponentialBackoffRetry(int baseSleepTimeMs, int maxSleepTimeMs, int maxRetries) {
        RetryPolicy retryPolicy = new BoundedExponentialBackoffRetry(baseSleepTimeMs, maxSleepTimeMs, maxRetries);

        return retryPolicy;
    }

    // 指定最大重试次数的重试
    public RetryPolicy createRetryNTimes(int count, int sleepMsBetweenRetries) {
        RetryPolicy retryPolicy = new RetryNTimes(count, sleepMsBetweenRetries);

        return retryPolicy;
    }

    // 永远重试
    public RetryPolicy createRetryForever(int retryIntervalMs) {
        RetryPolicy retryPolicy = new RetryForever(retryIntervalMs);

        return retryPolicy;
    }

    // 一直重试，直到达到规定的时间 
    public RetryPolicy createRetryUntilElapsed(int maxElapsedTimeMs, int sleepMsBetweenRetries) {
        RetryPolicy retryPolicy = new RetryUntilElapsed(maxElapsedTimeMs, sleepMsBetweenRetries);

        return retryPolicy;
    }

    // 创建ZooKeeper客户端实例
    public void create(String connectString, int sessionTimeoutMs, int connectionTimeoutMs, RetryPolicy retryPolicy) {
        LOG.info("Start to initialize Curator..");

        if (curator != null) {
            throw new CuratorException("Curator isn't null, it has been initialized already");
        }

        curator = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
    }

    // 启动ZooKeeper客户端
    public void start() throws Exception {
        LOG.info("Start Curator...");

        validateClosedStatus();

        curator.start();
    }

    // 启动ZooKeeper客户端，直到第一次连接成功
    public void startAndBlock() throws Exception {
        LOG.info("start and block Curator...");

        validateClosedStatus();

        curator.start();
        curator.blockUntilConnected();
    }

    // 启动ZooKeeper客户端，直到第一次连接成功，为每一次连接配置超时
    public void startAndBlock(int maxWaitTime, TimeUnit units) throws Exception {
        LOG.info("start and block Curator...");

        validateClosedStatus();

        curator.start();
        curator.blockUntilConnected(maxWaitTime, units);
    }

    // 关闭ZooKeeper客户端连接
    public void close() throws Exception {
        LOG.info("Start to close Curator...");

        validateStartedStatus();

        curator.close();
    }

    // 获取ZooKeeper客户端是否初始化
    public boolean isInitialized() {
        return curator != null;
    }

    // 获取ZooKeeper客户端连接是否正常
    public boolean isStarted() {
        return curator.getState() == CuratorFrameworkState.STARTED;
    }

    // 检查ZooKeeper是否是启动状态
    public void validateStartedStatus() throws Exception {
        if (curator == null) {
            throw new CuratorException("Curator is null");
        }

        if (!isStarted()) {
            throw new CuratorException("Curator is closed");
        }
    }

    // 检查ZooKeeper是否是关闭状态
    public void validateClosedStatus() throws Exception {
        if (curator == null) {
            throw new CuratorException("Curator is null");
        }

        if (isStarted()) {
            throw new CuratorException("Curator is started");
        }
    }

    // 获取ZooKeeper客户端
    public CuratorFramework getCurator() {
        return curator;
    }

    // 判断路径是否存在
    public boolean pathExist(String path) throws Exception {
        return getPathStat(path) != null;
    }

    // 判断stat是否存在
    public Stat getPathStat(String path) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        ExistsBuilder builder = curator.checkExists();
        if (builder == null) {
            return null;
        }

        Stat stat = builder.forPath(path);

        return stat;
    }

    // 创建路径
    public void createPath(String path) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().forPath(path, null);
    }

    // 创建路径，并写入数据
    public void createPath(String path, byte[] data) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().forPath(path, data);
    }

    // 创建路径
    public void createPath(String path, CreateMode mode) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, null);
    }

    // 创建路径，并写入数据
    public void createPath(String path, byte[] data, CreateMode mode) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data);
    }

    // 删除路径
    public void deletePath(String path) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.delete().deletingChildrenIfNeeded().forPath(path);
    }

    // 获取子节点名称列表
    public List<String> getChildNameList(String path) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        return curator.getChildren().forPath(path);
    }

    // 获取子节点路径列表
    public List<String> getChildPathList(String path) throws Exception {
        List<String> childNameList = getChildNameList(path);

        List<String> childPathList = new ArrayList<String>();
        for (String childName : childNameList) {
            String childPath = path + "/" + childName;
            childPathList.add(childPath);
        }

        return childPathList;
    }

    // 组装根节点路径
    public String getRootPath(String prefix) {
        return "/" + prefix;
    }

    // 组装节点路径
    public String getPath(String prefix, String key) {
        return "/" + prefix + "/" + key;
    }
}