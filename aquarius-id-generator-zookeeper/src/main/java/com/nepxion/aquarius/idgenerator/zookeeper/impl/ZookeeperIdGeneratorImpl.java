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

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.curator.constant.CuratorConstant;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.property.AquariusProperties;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;
import com.nepxion.aquarius.idgenerator.zookeeper.constant.ZookeeperIdGeneratorConstant;

@Component("zookeeperIdGeneratorImpl")
public class ZookeeperIdGeneratorImpl implements ZookeeperIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperIdGeneratorImpl.class);

    private AquariusProperties properties;
    private CuratorFramework curator;
    private String rootPath;

    @PostConstruct
    public void initialize() {
        try {
            properties = CuratorHandler.createPropertyConfig(CuratorConstant.CONFIG_FILE);
            curator = CuratorHandler.createCurator(properties);

            rootPath = properties.getString(ZookeeperIdGeneratorConstant.ROOT_PATH);
            if (!CuratorHandler.pathExist(curator, rootPath)) {
                CuratorHandler.createPath(curator, rootPath, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            LOG.error("Initialize Curator failed", e);
        }
    }

    @Override
    public int nextSequenceId(String sequenceName) throws Exception {
        String path = getPath(sequenceName);

        // 并发过快，这里会抛“节点已经存在”的错误，当节点存在时候，就不会创建，所以不必打印异常
        try {
            if (!CuratorHandler.pathExist(curator, path)) {
                CuratorHandler.createPath(curator, path, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {

        }

        return curator.setData().withVersion(-1).forPath(path, "".getBytes()).getVersion();
    }

    private String getPath(String sequenceName) {
        return rootPath + "/" + sequenceName;
    }
}