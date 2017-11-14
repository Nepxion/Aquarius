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
    @Cacheable(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = 60000L)
    String doA(String id1, String id2);

    @CacheEvict(value = "aquarius", key = "#id1 + \"-\" + #id2", allEntries = false, beforeInvocation = false)
    String doB(String id1, String id2);

    @CachePut(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = 60000L)
    String doC(String id1, String id2);
}