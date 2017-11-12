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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nepxion.aquarius.lock.annotation.WriteLock;

@Service("myService4Impl")
public class MyService4Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService4Impl.class);

    @WriteLock(key = "#id1 + \"-\" + #id2", leaseTime = 15000, waitTime = 60000, async = false, fair = false)
    public void doW(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doW");
    }
}