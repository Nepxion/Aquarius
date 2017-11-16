package com.nepxion.aquarius.lock.local.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.lock.delegate.LockDelegate;
import com.nepxion.aquarius.lock.local.condition.LocalLockCondition;
import com.nepxion.aquarius.lock.local.constant.LocalLockConstant;
import com.nepxion.aquarius.lock.local.delegate.LocalLockDelegate;

@Configuration
@ComponentScan(basePackages = { "com.nepxion.aquarius.common" })
public class LocalLockConfig {
    @Bean(name = LocalLockConstant.DELEGATE_VALUE)
    @Conditional(LocalLockCondition.class)
    public LockDelegate localLockDelegate() {
        return new LocalLockDelegate();
    }
}