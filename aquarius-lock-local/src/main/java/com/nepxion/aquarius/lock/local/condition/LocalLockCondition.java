package com.nepxion.aquarius.lock.local.condition;

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
import com.nepxion.aquarius.lock.local.constant.LocalLockConstant;

public class LocalLockCondition extends AquariusCondition {
    public LocalLockCondition() {
        super(LockConstant.DELEGATE_KEY, LocalLockConstant.DELEGATE_VALUE);
    }
}