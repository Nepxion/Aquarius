package com.nepxion.aquarius.limit.aop;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.nepxion.aquarius.limit.annotation.EnableLimit;
import com.nepxion.matrix.selector.AbstractImportSelector;

@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class LimitImportSelector extends AbstractImportSelector<EnableLimit> {
    @Override
    protected boolean isEnabled() {
        return new RelaxedPropertyResolver(getEnvironment()).getProperty("limit.enabled", Boolean.class, Boolean.TRUE);
    }
}