package com.nepxion.aquarius.idgenerator.zookeeper.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;

public class ZookeeperIdGeneratorImpl implements ZookeeperIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperIdGeneratorImpl.class);

    private static final int MAX_BATCH_COUNT = 1000;

    @Autowired
    private CuratorHandler curatorHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + ":false}")
    private Boolean frequentLogPrint;

    @PreDestroy
    public void destroy() {
        try {
            curatorHandler.close();
        } catch (Exception e) {
            throw new AquariusException("Close Curator failed", e);
        }
    }

    @Override
    public String nextSequenceId(String name, String key) throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("key is null or empty");
        }

        String compositeKey = KeyUtil.getCompositeKey(prefix, name, key);

        return nextSequenceId(compositeKey);
    }

    @Override
    public String nextSequenceId(String compositeKey) throws Exception {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new AquariusException("Composite key is null or empty");
        }

        curatorHandler.validateStartedStatus();

        String path = curatorHandler.getPath(prefix, compositeKey);

        // 并发过快，这里会抛“节点已经存在”的错误，当节点存在时候，就不会创建，所以不必打印异常
        try {
            if (!curatorHandler.pathExist(path)) {
                curatorHandler.createPath(path, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {

        }

        CuratorFramework curator = curatorHandler.getCurator();
        int nextSequenceId = curator.setData().withVersion(-1).forPath(path, "".getBytes()).getVersion();

        if (frequentLogPrint) {
            LOG.info("Next sequenceId id is {} for key={}", nextSequenceId, compositeKey);
        }

        return String.valueOf(nextSequenceId);
    }

    @Override
    public String[] nextSequenceIds(String name, String key, int count) throws Exception {
        if (count <= 0 || count > MAX_BATCH_COUNT) {
            throw new AquariusException(String.format("Count can't be greater than %d or less than 0", MAX_BATCH_COUNT));
        }

        String[] nextSequenceIds = new String[count];
        for (int i = 0; i < count; i++) {
            nextSequenceIds[i] = nextSequenceId(name, key);
        }

        return nextSequenceIds;
    }

    @Override
    public String[] nextSequenceIds(String compositeKey, int count) throws Exception {
        if (count <= 0 || count > MAX_BATCH_COUNT) {
            throw new AquariusException(String.format("Count can't be greater than %d or less than 0", MAX_BATCH_COUNT));
        }

        String[] nextSequenceIds = new String[count];
        for (int i = 0; i < count; i++) {
            nextSequenceIds[i] = nextSequenceId(compositeKey);
        }

        return nextSequenceIds;
    }
}