package com.nepxion.aquarius.idgenerator.aop;

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

import com.nepxion.aquarius.idgenerator.annotation.EnableLocalIdGenerator;
import com.nepxion.aquarius.idgenerator.constant.IdGeneratorConstant;
import com.nepxion.matrix.selector.AbstractImportSelector;
import com.nepxion.matrix.selector.RelaxedPropertyResolver;

@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class LocalIdGeneratorImportSelector extends AbstractImportSelector<EnableLocalIdGenerator> {
    @Override
    protected boolean isEnabled() {
        return new RelaxedPropertyResolver(getEnvironment()).getProperty(IdGeneratorConstant.IDGENERATOR_ENABLED, Boolean.class, Boolean.TRUE);
    }
}