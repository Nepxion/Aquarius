package com.nepxion.aquarius.limit.local.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.limit.LimitDelegate;
import com.nepxion.aquarius.limit.LimitExecutor;
import com.nepxion.aquarius.limit.local.condition.LocalLimitCondition;
import com.nepxion.aquarius.limit.local.impl.LocalLimitDelegateImpl;
import com.nepxion.aquarius.limit.local.impl.LocalLimitExecutorImpl;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class LocalLimitConfig {
    @Bean(name = "localLimitDelegate")
    @Conditional(LocalLimitCondition.class)
    public LimitDelegate localLimitDelegate() {
        return new LocalLimitDelegateImpl();
    }

    @Bean(name = "localLimitExecutor")
    @Conditional(LocalLimitCondition.class)
    public LimitExecutor localLimitExecutor() {
        return new LocalLimitExecutorImpl();
    }
}