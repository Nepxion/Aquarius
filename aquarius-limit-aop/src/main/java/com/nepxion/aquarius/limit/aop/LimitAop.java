package com.nepxion.aquarius.limit.aop;

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

import com.nepxion.aquarius.limit.constant.LimitConstant;

@Component("limitAop")
public class LimitAop {
    @Value("${" + LimitConstant.LIMIT_SCAN_PACKAGES + "}")
    private String scanPackages;

    @Bean("limitAutoScanProxy")
    public LimitAutoScanProxy limitAutoScanProxy() {
        return new LimitAutoScanProxy(scanPackages);
    }
}