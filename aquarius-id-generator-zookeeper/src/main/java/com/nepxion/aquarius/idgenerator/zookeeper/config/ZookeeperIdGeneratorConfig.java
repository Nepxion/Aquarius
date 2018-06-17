package com.nepxion.aquarius.idgenerator.zookeeper.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.curator.handler.CuratorHandlerImpl;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;
import com.nepxion.aquarius.idgenerator.zookeeper.impl.ZookeeperIdGeneratorImpl;

@Configuration
public class ZookeeperIdGeneratorConfig {
    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Bean
    public ZookeeperIdGenerator zookeeperIdGenerator() {
        return new ZookeeperIdGeneratorImpl();
    }

    @Bean
    public CuratorHandler curatorHandler() {
        return new CuratorHandlerImpl(prefix);
    }
}