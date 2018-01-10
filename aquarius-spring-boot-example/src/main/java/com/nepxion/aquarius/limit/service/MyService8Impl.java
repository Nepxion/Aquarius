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

import com.nepxion.aquarius.limit.annotation.Limit;

@Service("myService8Impl")
public class MyService8Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService8Impl.class);

    @Limit(name = "limit", key = "#id1 + \"-\" + #id2", limitPeriod = 10, limitCount = 5)
    public String doB(String id1, String id2) {
        LOG.info("doB");

        return "B";
    }
}