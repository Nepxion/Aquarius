package com.nepxion.aquarius.lock.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("myService3Impl")
public class MyService3Impl implements MyService3 {
    private static final Logger LOG = LoggerFactory.getLogger(MyService3Impl.class);

    @Override
    public String doR(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doR - read lock is got");

        return "R";
    }
}