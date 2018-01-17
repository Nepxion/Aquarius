package com.nepxion.aquarius.lock;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.lock.entity.LockType;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.lock" })
public class ReadWriteLockApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ReadWriteLockApplication.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ReadWriteLockApplication.class, args);

        LockExecutor<Object> lockExecutor = AquariusContextAware.getBean(LockExecutor.class);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get write lock...");
                // 写锁逻辑，最高持锁15秒，睡眠10秒，10秒后释放读锁
                Object lock = null;
                try {
                    lock = lockExecutor.tryLock(LockType.WRITE_LOCK, "lock", "X-Y", 15000L, 60000L, false, false);
                    if (lock != null) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(10000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        LOG.info("doW - write lock is got");
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
        }, 0L, 600000L);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get read lock...");
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 读锁逻辑，最高持锁5秒，睡眠2秒，2秒后释放读锁
                            Object lock = null;
                            try {
                                lock = lockExecutor.tryLock(LockType.READ_LOCK, "lock", "X-Y", 5000L, 60000L, false, false);
                                if (lock != null) {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(2000L);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    LOG.info("doR - read lock is got");
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
        }, 2000L, 2000L);
    }

    @Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8090);

        return tomcatFactory;
    }
}