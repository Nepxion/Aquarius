package com.nepxion.aquarius.cache.redis.condition;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.cache.constant.CacheConstant;
import com.nepxion.aquarius.cache.redis.constant.RedisCacheConstant;
import com.nepxion.aquarius.common.condition.AquariusCondition;

public class RedisCacheCondition extends AquariusCondition {
    public RedisCacheCondition() {
        super(CacheConstant.DELEGATE_KEY, RedisCacheConstant.DELEGATE_VALUE);
    }
}