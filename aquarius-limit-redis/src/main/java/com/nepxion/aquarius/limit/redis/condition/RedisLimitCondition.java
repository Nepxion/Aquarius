package com.nepxion.aquarius.limit.redis.condition;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.common.condition.AquariusCondition;
import com.nepxion.aquarius.limit.constant.LimitConstant;
import com.nepxion.aquarius.limit.redis.constant.RedisLimitConstant;

public class RedisLimitCondition extends AquariusCondition {
    public RedisLimitCondition() {
        super(LimitConstant.DELEGATE_KEY, RedisLimitConstant.DELEGATE_VALUE);
    }
}