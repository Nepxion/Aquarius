package com.nepxion.aquarius.lock.test.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.lock.annotation.Lock;

public interface MyService1 {
    @Lock(key = "#id1 + \"-\" + #id2", leaseTime = 5000, waitTime = 60000, async = false, fair = false)
    void doA(String id1, String id2);

    void doB(String id1, String id2);
}