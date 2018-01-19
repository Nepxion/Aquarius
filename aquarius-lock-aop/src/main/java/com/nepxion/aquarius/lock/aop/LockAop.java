package com.nepxion.aquarius.lock.aop;

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

import com.nepxion.aquarius.lock.constant.LockConstant;

@Component("lockAop")
public class LockAop {
    @Value("${" + LockConstant.LOCK_SCAN_PACKAGES + ":}")
    private String scanPackages;

    @Bean("lockAutoScanProxy")
    public LockAutoScanProxy lockAutoScanProxy() {
        return new LockAutoScanProxy(scanPackages);
    }
}