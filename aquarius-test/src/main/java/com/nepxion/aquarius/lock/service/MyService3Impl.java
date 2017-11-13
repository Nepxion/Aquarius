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

@Service("myService3Impl")
public class MyService3Impl implements MyService3 {
    private static final Logger LOG = LoggerFactory.getLogger(MyService3Impl.class);

    @Override
    public void doR(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doR");
    }
}