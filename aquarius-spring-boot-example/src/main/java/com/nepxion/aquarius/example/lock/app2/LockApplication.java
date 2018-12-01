package com.nepxion.aquarius.example.lock.app2;

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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.annotation.EnableLock;
import com.nepxion.aquarius.lock.entity.LockType;

@SpringBootApplication
@EnableLock
@ComponentScan(basePackages = { "com.nepxion.aquarius.example.lock.service" })
public class LockApplication {
    private static final Logger LOG = LoggerFactory.getLogger(LockApplication.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(LockApplication.class, args);

        LockExecutor<Object> lockExecutor = applicationContext.getBean(LockExecutor.class);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Object lock = null;
                    try {
                        lock = lockExecutor.tryLock(LockType.LOCK, "lock", "X-Y", 5000L, 60000L, false, false);
                        if (lock != null) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(2000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            LOG.info("doA - lock is got");
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
                }
            }).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Object lock = null;
                    try {
                        lock = lockExecutor.tryLock(LockType.LOCK, "lock", "X-Y", 5000L, 60000L, false, false);
                        if (lock != null) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(2000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            LOG.info("doC - lock is got");
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
                }
            }).start();
        }
    }

    // 如下方式，只支持Spring Cloud F版以前的版本
    /*@Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8088);

        return tomcatFactory;
    }*/
}