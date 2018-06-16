package com.nepxion.aquarius.idgenerator.aop;

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

import com.nepxion.aquarius.idgenerator.annotation.EnableIdGenerator;
import com.nepxion.matrix.selector.AbstractImportSelector;

@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class IdGeneratorImportSelector extends AbstractImportSelector<EnableIdGenerator> {
    @Override
    protected boolean isEnabled() {
        return new RelaxedPropertyResolver(getEnvironment()).getProperty("idgenerator.enabled", Boolean.class, Boolean.TRUE);
    }
}