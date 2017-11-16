package com.nepxion.aquarius.lock.redis.condition;

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
import com.nepxion.aquarius.lock.constant.LockConstant;
import com.nepxion.aquarius.lock.redis.constant.RedisLockConstant;

public class RedisLockCondition extends AquariusCondition {
    public RedisLockCondition() {
        super(LockConstant.DELEGATE_KEY, RedisLockConstant.DELEGATE_VALUE);
    }
}