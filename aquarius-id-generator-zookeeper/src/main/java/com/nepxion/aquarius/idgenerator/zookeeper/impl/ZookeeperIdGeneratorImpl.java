package com.nepxion.aquarius.idgenerator.zookeeper.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;

@Component("zookeeperIdGeneratorImpl")
public class ZookeeperIdGeneratorImpl implements ZookeeperIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperIdGeneratorImpl.class);

    @Autowired
    private CuratorFramework curator;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    @Override
    public int nextSequenceId(String name, String key) throws Exception {
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
    public int nextSequenceId(String compositeKey) throws Exception {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new AquariusException("Composite key is null or empty");
        }

        if (!CuratorHandler.isStarted(curator)) {
            throw new AquariusException("Curator isn't started");
        }

        String path = CuratorHandler.getPath(prefix, compositeKey);

        // 并发过快，这里会抛“节点已经存在”的错误，当节点存在时候，就不会创建，所以不必打印异常
        try {
            if (!CuratorHandler.pathExist(curator, path)) {
                CuratorHandler.createPath(curator, path, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {

        }

        int nextSequenceId = curator.setData().withVersion(-1).forPath(path, "".getBytes()).getVersion();

        if (frequentLogPrint) {
            LOG.info("Next sequenceId id is {} for key={}", nextSequenceId, compositeKey);
        }

        return nextSequenceId;
    }
}