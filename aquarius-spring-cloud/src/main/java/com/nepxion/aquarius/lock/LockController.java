package com.nepxion.aquarius.lock;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nepxion.aquarius.lock.entity.LockType;

@RestController
public class LockController {
    private static final Logger LOG = LoggerFactory.getLogger(LockController.class);

    @SuppressWarnings("rawtypes")
    @Autowired
    private LockExecutor lockExecutor;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/tryLock", method = RequestMethod.GET)
    public String tryLock(@RequestParam String lockType, @RequestParam String name, @RequestParam String key, @RequestParam long leaseTime, @RequestParam long waitTime, @RequestParam boolean async, @RequestParam boolean fair) {
        Object lock = null;
        try {
            lock = lockExecutor.tryLock(LockType.fromString(lockType), name, key, leaseTime, waitTime, async, fair);
            if (lock != null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                LOG.info("doX - lock is got");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lockExecutor.unlock(lock);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "lock";
    }
}