package com.nepxion.aquarius.common.curator.handler;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

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
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.constant.CuratorConstant;
import com.nepxion.aquarius.common.curator.entity.RetryTypeEnum;
import com.nepxion.aquarius.common.curator.exception.CuratorException;

public class CuratorHandlerImpl implements CuratorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CuratorHandlerImpl.class);

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + CuratorConstant.CONNECT_STRING + "}")
    private String connectString;

    @Value("${" + CuratorConstant.SESSION_TIMEOUT_MS + ":15000}")
    private int sessionTimeoutMs;

    @Value("${" + CuratorConstant.CONNECTION_TIMEOUT_MS + ":15000}")
    private int connectionTimeoutMs;

    @Value("${" + CuratorConstant.RETRY_TYPE + ":retryNTimes}")
    private String retryType;

    @Value("${" + CuratorConstant.RETRY_TYPE_EXPONENTIAL_BACKOFF_RETRY_BASE_SLEEP_TIME_MS + ":2000}")
    private int exponentialBackoffRetryBaseSleepTimeMs;

    @Value("${" + CuratorConstant.RETRY_TYPE_EXPONENTIAL_BACKOFF_RETRY_MAX_RETRIES + ":10}")
    private int exponentialBackoffRetryMaxRetries;

    @Value("${" + CuratorConstant.RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY_BASE_SLEEP_TIME_MS + ":2000}")
    private int boundedExponentialBackoffRetryBaseSleepTimeMs;

    @Value("${" + CuratorConstant.RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY_MAX_SLEEP_TIME_MS + ":60000}")
    private int boundedExponentialBackoffRetryMaxSleepTimeMs;

    @Value("${" + CuratorConstant.RETRY_TYPE_BOUNDED_EXPONENTIAL_BACKOFF_RETRY_MAX_RETRIES + ":10}")
    private int boundedExponentialBackoffRetryMaxRetries;

    @Value("${" + CuratorConstant.RETRY_TYPE_RETRY_NTIMES_COUNT + ":10}")
    private int retryNTimesCount;

    @Value("${" + CuratorConstant.RETRY_TYPE_RETRY_NTIMES_SLEEP_MS_BETWEEN_RETRIES + ":2000}")
    private int retryNTimesSleepMsBetweenRetries;

    @Value("${" + CuratorConstant.RETRY_TYPE_RETRY_FOREVER_RETRY_INTERVAL_MS + ":1000}")
    private int retryForeverRetryIntervalMs;

    @Value("${" + CuratorConstant.RETRY_TYPE_RETRY_UNTIL_ELAPSED_MAX_ELAPSED_TIME_MS + ":60000}")
    private int retryUntilElapsedMaxElapsedTimeMs;

    @Value("${" + CuratorConstant.RETRY_TYPE_RETRY_UNTIL_ELAPSED_SLEEP_MS_BETWEEN_RETRIES + ":2000}")
    private int retryUntilElapsedSleepMsBetweenRetries;

    private CuratorFramework curator;

    // 创建Curator，并初始化根节点
    @PostConstruct
    private void initialize() throws Exception {
        try {
            create();

            String rootPath = getRootPath(prefix);
            if (!pathExist(rootPath)) {
                createPath(rootPath, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            LOG.error("Initialize Curator failed", e);

            throw e;
        }
    }

    // 创建Curator
    private void create() throws Exception {
        if (StringUtils.isEmpty(connectString)) {
            throw new CuratorException(CuratorConstant.CONNECT_STRING + " can't be null or empty");
        }

        RetryPolicy retryPolicy = null;
        RetryTypeEnum retryTypeEnum = RetryTypeEnum.fromString(retryType);
        switch (retryTypeEnum) {
            case EXPONENTIAL_BACKOFF_RETRY: {
                retryPolicy = createExponentialBackoffRetry(exponentialBackoffRetryBaseSleepTimeMs, exponentialBackoffRetryMaxRetries);
                break;
            }
            case BOUNDED_EXPONENTIAL_BACKOFF_RETRY: {
                retryPolicy = createBoundedExponentialBackoffRetry(boundedExponentialBackoffRetryBaseSleepTimeMs, boundedExponentialBackoffRetryMaxSleepTimeMs, boundedExponentialBackoffRetryMaxRetries);
                break;
            }
            case RETRY_NTIMES: {
                retryPolicy = createRetryNTimes(retryNTimesCount, retryNTimesSleepMsBetweenRetries);
                break;
            }
            case RETRY_FOREVER: {
                retryPolicy = createRetryForever(retryForeverRetryIntervalMs);
                break;
            }
            case RETRY_UNTIL_ELAPSED: {
                retryPolicy = createRetryUntilElapsed(retryUntilElapsedMaxElapsedTimeMs, retryUntilElapsedSleepMsBetweenRetries);
                break;
            }
        }

        if (retryPolicy == null) {
            throw new CuratorException("Invalid config value for retryType=" + retryType);
        }

        create(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);

        startAndBlock();
    }

    // 重试指定的次数, 且每一次重试之间停顿的时间逐渐增加
    private RetryPolicy createExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);

        return retryPolicy;
    }

    // 重试指定的次数, 且每一次重试之间停顿的时间逐渐增加，增加了最大重试次数的控制
    private RetryPolicy createBoundedExponentialBackoffRetry(int baseSleepTimeMs, int maxSleepTimeMs, int maxRetries) {
        RetryPolicy retryPolicy = new BoundedExponentialBackoffRetry(baseSleepTimeMs, maxSleepTimeMs, maxRetries);

        return retryPolicy;
    }

    // 指定最大重试次数的重试
    private RetryPolicy createRetryNTimes(int count, int sleepMsBetweenRetries) {
        RetryPolicy retryPolicy = new RetryNTimes(count, sleepMsBetweenRetries);

        return retryPolicy;
    }

    // 永远重试
    private RetryPolicy createRetryForever(int retryIntervalMs) {
        RetryPolicy retryPolicy = new RetryForever(retryIntervalMs);

        return retryPolicy;
    }

    // 一直重试，直到达到规定的时间 
    private RetryPolicy createRetryUntilElapsed(int maxElapsedTimeMs, int sleepMsBetweenRetries) {
        RetryPolicy retryPolicy = new RetryUntilElapsed(maxElapsedTimeMs, sleepMsBetweenRetries);

        return retryPolicy;
    }

    // 创建ZooKeeper客户端实例
    private void create(String connectString, int sessionTimeoutMs, int connectionTimeoutMs, RetryPolicy retryPolicy) {
        LOG.info("Start to initialize Curator..");

        if (curator != null) {
            throw new CuratorException("Curator isn't null, it has been initialized already");
        }

        curator = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
    }

    // 启动ZooKeeper客户端
    @Override
    public void start() {
        LOG.info("Start Curator...");

        validateClosedStatus();

        curator.start();
    }

    // 启动ZooKeeper客户端，直到第一次连接成功
    @Override
    public void startAndBlock() throws Exception {
        LOG.info("start and block Curator...");

        validateClosedStatus();

        curator.start();
        curator.blockUntilConnected();
    }

    // 启动ZooKeeper客户端，直到第一次连接成功，为每一次连接配置超时
    @Override
    public void startAndBlock(int maxWaitTime, TimeUnit units) throws Exception {
        LOG.info("start and block Curator...");

        validateClosedStatus();

        curator.start();
        curator.blockUntilConnected(maxWaitTime, units);
    }

    // 关闭ZooKeeper客户端连接
    @Override
    public void close() {
        LOG.info("Start to close Curator...");

        validateStartedStatus();

        curator.close();
    }

    // 获取ZooKeeper客户端是否初始化
    @Override
    public boolean isInitialized() {
        return curator != null;
    }

    // 获取ZooKeeper客户端连接是否正常
    @Override
    public boolean isStarted() {
        return curator.getState() == CuratorFrameworkState.STARTED;
    }

    // 检查ZooKeeper是否是启动状态
    @Override
    public void validateStartedStatus() {
        if (curator == null) {
            throw new CuratorException("Curator isn't initialized");
        }

        if (!isStarted()) {
            throw new CuratorException("Curator is closed");
        }
    }

    // 检查ZooKeeper是否是关闭状态
    @Override
    public void validateClosedStatus() {
        if (curator == null) {
            throw new CuratorException("Curator isn't initialized");
        }

        if (isStarted()) {
            throw new CuratorException("Curator is started");
        }
    }

    // 获取ZooKeeper客户端
    @Override
    public CuratorFramework getCurator() {
        return curator;
    }

    // 判断路径是否存在
    @Override
    public boolean pathExist(String path) throws Exception {
        return getPathStat(path) != null;
    }

    // 判断stat是否存在
    @Override
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
    @Override
    public void createPath(String path) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().forPath(path, null);
    }

    // 创建路径，并写入数据
    @Override
    public void createPath(String path, byte[] data) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().forPath(path, data);
    }

    // 创建路径
    @Override
    public void createPath(String path, CreateMode mode) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, null);
    }

    // 创建路径，并写入数据
    @Override
    public void createPath(String path, byte[] data, CreateMode mode) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data);
    }

    // 删除路径
    @Override
    public void deletePath(String path) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        curator.delete().deletingChildrenIfNeeded().forPath(path);
    }

    // 获取子节点名称列表
    @Override
    public List<String> getChildNameList(String path) throws Exception {
        validateStartedStatus();
        PathUtils.validatePath(path);

        return curator.getChildren().forPath(path);
    }

    // 获取子节点路径列表
    @Override
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
    @Override
    public String getRootPath(String prefix) {
        return "/" + prefix;
    }

    // 组装节点路径
    @Override
    public String getPath(String prefix, String key) {
        return "/" + prefix + "/" + key;
    }
}