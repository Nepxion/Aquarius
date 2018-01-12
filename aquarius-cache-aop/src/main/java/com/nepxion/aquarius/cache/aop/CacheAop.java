package com.nepxion.aquarius.cache.aop;

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
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.cache.constant.CacheConstant;

@Component("cacheAop")
public class CacheAop {
    @Value("${" + CacheConstant.CACHE_SCAN_PACKAGES + "}")
    private String scanPackages;

    @Bean("cacheAutoScanProxy")
    public CacheAutoScanProxy cacheAutoScanProxy() {
        return new CacheAutoScanProxy(scanPackages);
    }
}