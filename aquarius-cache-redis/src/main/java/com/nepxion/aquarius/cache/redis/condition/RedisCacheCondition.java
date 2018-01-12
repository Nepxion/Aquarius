package com.nepxion.aquarius.cache.redis.condition;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.aquarius.cache.constant.CacheConstant;
import com.nepxion.aquarius.common.condition.AquariusCondition;

public class RedisCacheCondition extends AquariusCondition {
    public RedisCacheCondition() {
        super(CacheConstant.CACHE_TYPE, CacheConstant.CACHE_TYPE_REDIS);
    }
}