package com.nepxion.aquarius.idgenerator.local.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.idgenerator.local.LocalIdGenerator;
import com.nepxion.aquarius.idgenerator.local.impl.LocalIdGeneratorImpl;

@Configuration
public class LocalIdGeneratorConfiguration {
    @Bean
    public LocalIdGenerator localIdGenerator() {
        return new LocalIdGeneratorImpl();
    }
}