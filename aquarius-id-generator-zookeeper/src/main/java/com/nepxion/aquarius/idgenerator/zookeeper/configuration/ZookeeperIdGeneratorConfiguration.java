package com.nepxion.aquarius.idgenerator.zookeeper.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.curator.handler.CuratorHandlerImpl;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;
import com.nepxion.aquarius.idgenerator.zookeeper.impl.ZookeeperIdGeneratorImpl;

@Configuration
public class ZookeeperIdGeneratorConfiguration {
    @Bean
    public ZookeeperIdGenerator zookeeperIdGenerator() {
        return new ZookeeperIdGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public CuratorHandler curatorHandler() {
        return new CuratorHandlerImpl();
    }
}