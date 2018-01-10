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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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
@Api(tags = { "分布式锁接口" })
public class LockController {
    private static final Logger LOG = LoggerFactory.getLogger(LockController.class);

    @SuppressWarnings("rawtypes")
    @Autowired
    private LockExecutor lockExecutor;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/tryLock", method = RequestMethod.GET)
    @ApiOperation(value = "获取分布式锁", notes = "尝试获取锁，如果获取到锁，则返回锁对象，如果未获取到锁，则返回空", response = String.class, httpMethod = "GET")
    public String tryLock(
            @RequestParam @ApiParam(value = "锁的类型", required = true, allowableValues = "Lock, ReadLock, WriteLock", defaultValue = "Lock") String lockType,
            @RequestParam @ApiParam(value = "锁的名字", required = true, defaultValue = "lock") String name,
            @RequestParam @ApiParam(value = "锁的Key", required = true, defaultValue = "x-y") String key,
            @RequestParam @ApiParam(value = "持锁时间(单位毫秒)", required = true, defaultValue = "5000") long leaseTime,
            @RequestParam @ApiParam(value = "没有获取到锁时，等待时间(单位毫秒)", required = true, defaultValue = "60000") long waitTime,
            @RequestParam @ApiParam(value = "是否采用锁的异步执行方式", required = true, defaultValue = "false") boolean async,
            @RequestParam @ApiParam(value = "是否采用公平锁", required = true, defaultValue = "false") boolean fair) {
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