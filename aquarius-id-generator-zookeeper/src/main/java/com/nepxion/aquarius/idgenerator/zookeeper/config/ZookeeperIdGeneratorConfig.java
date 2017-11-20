package com.nepxion.aquarius.idgenerator.zookeeper.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class ZookeeperIdGeneratorConfig {
    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Bean(name = "curator")
    public CuratorFramework curator() {
        return CuratorHandler.createDefaultCurator(prefix);
    }
}