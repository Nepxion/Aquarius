package com.nepxion.aquarius.lock.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.aquarius.lock.annotation.Lock;

public interface MyService1 {
    @Lock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 5000L, waitTime = 60000L, async = false, fair = false)
    String doA(String id1, String id2);

    String doB(String id1, String id2);
}