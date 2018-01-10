package com.nepxion.aquarius.lock;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import io.swagger.annotations.Api;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nepxion.aquarius.lock.annotation.Lock;
import com.nepxion.aquarius.lock.annotation.ReadLock;
import com.nepxion.aquarius.lock.annotation.WriteLock;

@RestController
@Api(tags = { "分布式锁注解接口" })
public class LockAopController {
    private static final Logger LOG = LoggerFactory.getLogger(LockAopController.class);

    @RequestMapping(value = "/doA", method = RequestMethod.GET)
    @Lock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 5000L, waitTime = 60000L, async = false, fair = false)
    public String doA(@RequestParam String id1, @RequestParam String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doA - lock is got");

        return "A";
    }

    @RequestMapping(value = "/doB", method = RequestMethod.GET)
    @ReadLock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 5000L, waitTime = 60000L, async = false, fair = false)
    public String doB(@RequestParam String id1, @RequestParam String id2) {
        LOG.info("doB");

        return "B";
    }

    @RequestMapping(value = "/doC", method = RequestMethod.GET)
    @WriteLock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 5000L, waitTime = 60000L, async = false, fair = false)
    public String doC(@RequestParam String id1, @RequestParam String id2) {
        LOG.info("doC");

        return "C";
    }
}