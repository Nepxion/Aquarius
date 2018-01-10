package com.nepxion.aquarius.limit.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("myService7Impl")
public class MyService7Impl implements MyService7 {
    private static final Logger LOG = LoggerFactory.getLogger(MyService7Impl.class);

    @Override
    public String doA(String id1, String id2) {
        LOG.info("doA");

        return "A";
    }
}