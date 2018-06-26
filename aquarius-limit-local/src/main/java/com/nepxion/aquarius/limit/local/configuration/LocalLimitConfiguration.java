package com.nepxion.aquarius.limit.local.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.limit.LimitDelegate;
import com.nepxion.aquarius.limit.LimitExecutor;
import com.nepxion.aquarius.limit.local.condition.LocalLimitCondition;
import com.nepxion.aquarius.limit.local.impl.GuavaLocalLimitExecutorImpl;
import com.nepxion.aquarius.limit.local.impl.LocalLimitDelegateImpl;

@Configuration
public class LocalLimitConfiguration {
    @Bean
    @Conditional(LocalLimitCondition.class)
    public LimitDelegate localLimitDelegate() {
        return new LocalLimitDelegateImpl();
    }

    @Bean
    @Conditional(LocalLimitCondition.class)
    public LimitExecutor localLimitExecutor() {
        return new GuavaLocalLimitExecutorImpl();

        // return new JdkLimitExecutorImpl();
    }
}