package com.nepxion.aquarius.limit.configuration;

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

import com.nepxion.aquarius.limit.aop.LimitAutoScanProxy;
import com.nepxion.aquarius.limit.aop.LimitInterceptor;
import com.nepxion.aquarius.limit.constant.LimitConstant;

@Configuration
public class LimitAopConfiguration {
    @Value("${" + LimitConstant.LIMIT_SCAN_PACKAGES + ":}")
    private String scanPackages;

    @Bean
    public LimitAutoScanProxy limitAutoScanProxy() {
        return new LimitAutoScanProxy(scanPackages);
    }

    @Bean
    public LimitInterceptor limitInterceptor() {
        return new LimitInterceptor();
    }
}