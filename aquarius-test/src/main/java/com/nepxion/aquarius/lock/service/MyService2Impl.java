package com.nepxion.aquarius.lock.service;

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

import com.nepxion.aquarius.lock.annotation.Lock;

@Service("myService2Impl")
public class MyService2Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService2Impl.class);

    @Lock(key = "#id1 + \"-\" + #id2", leaseTime = 5000, waitTime = 60000, async = false, fair = false)
    public void doC(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doC");
    }

    public void doD(String id1, String id2) {
        LOG.info("doD");
    }
}