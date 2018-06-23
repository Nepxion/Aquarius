package com.nepxion.aquarius.cache.configuration;

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

import com.nepxion.aquarius.cache.aop.CacheAutoScanProxy;
import com.nepxion.aquarius.cache.aop.CacheInterceptor;
import com.nepxion.aquarius.cache.constant.CacheConstant;

@Configuration
public class CacheAopConfiguration {
    @Value("${" + CacheConstant.CACHE_SCAN_PACKAGES + ":}")
    private String scanPackages;

    @Bean
    public CacheAutoScanProxy cacheAutoScanProxy() {
        return new CacheAutoScanProxy(scanPackages);
    }

    @Bean
    public CacheInterceptor cacheInterceptor() {
        return new CacheInterceptor();
    }
}