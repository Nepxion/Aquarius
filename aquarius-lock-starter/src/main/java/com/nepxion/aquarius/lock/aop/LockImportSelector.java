package com.nepxion.aquarius.lock.aop;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.nepxion.aquarius.lock.annotation.EnableLock;
import com.nepxion.matrix.selector.AbstractImportSelector;
import com.nepxion.matrix.selector.RelaxedPropertyResolver;

@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class LockImportSelector extends AbstractImportSelector<EnableLock> {
    @Override
    protected boolean isEnabled() {
        return new RelaxedPropertyResolver(getEnvironment()).getProperty("lock.enabled", Boolean.class, Boolean.TRUE);
    }
}