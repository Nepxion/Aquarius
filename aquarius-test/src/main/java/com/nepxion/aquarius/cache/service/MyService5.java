package com.nepxion.aquarius.cache.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;

public interface MyService5 {
    @Cacheable(value = "", key = "#id1 + \"-\" + #id2", expire = 60L)
    void doA(String id1, String id2);

    @CacheEvict(value = "", key = "#id1 + \"-\" + #id2", allEntries = false, beforeInvocation = false)
    void doB(String id1, String id2);

    @CachePut(value = "", key = "#id1 + \"-\" + #id2", expire = 60L)
    void doC(String id1, String id2);
}