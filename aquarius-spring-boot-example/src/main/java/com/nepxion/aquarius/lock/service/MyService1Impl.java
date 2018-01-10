package com.nepxion.aquarius.lock.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("myService1Impl")
public class MyService1Impl implements MyService1 {
    private static final Logger LOG = LoggerFactory.getLogger(MyService1Impl.class);

    @Override
    public String doA(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doA - lock is got");

        return "A";
    }

    @Override
    public String doB(String id1, String id2) {
        LOG.info("doB");

        return "B";
    }
}