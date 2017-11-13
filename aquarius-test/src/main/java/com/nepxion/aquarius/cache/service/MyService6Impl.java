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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;

@Service("myService6Impl")
public class MyService6Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService6Impl.class);

    @Cacheable(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = 60L)
    public void doD(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doD");
    }

    @CacheEvict(value = "aquarius", key = "#id1 + \"-\" + #id2", allEntries = false, beforeInvocation = false)
    public void doE(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doE");
    }

    @CachePut(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = 60L)
    public void doF(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doF");
    }
}