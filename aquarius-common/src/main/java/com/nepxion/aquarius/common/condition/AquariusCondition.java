package com.nepxion.aquarius.common.condition;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.nepxion.aquarius.common.property.AquariusProperties;

public class AquariusCondition implements Condition {
    private String key;
    private String value;

    public AquariusCondition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AquariusProperties properties = context.getBeanFactory().getBean(AquariusProperties.class);

        String beanName = properties.getString(key);

        return StringUtils.equals(beanName, value);
    }
}