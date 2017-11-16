package com.nepxion.aquarius.common.property;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.constant.AquariusConstant;

@Component("propertiesManager")
public class AquariusPropertiesManager {
    private static final Logger LOG = LoggerFactory.getLogger(AquariusPropertiesManager.class);

    @Bean
    public AquariusProperties properties() {
        try {
            AquariusContent content = new AquariusContent(AquariusConstant.CONFIG_FILE);

            return new AquariusProperties(content.getContent());
        } catch (IOException e) {
            LOG.error("Read {} failed", AquariusConstant.CONFIG_FILE, e);
        }

        return null;
    }
}