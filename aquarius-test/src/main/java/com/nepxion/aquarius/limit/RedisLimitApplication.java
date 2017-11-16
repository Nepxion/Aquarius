package com.nepxion.aquarius.limit;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.limit.redis.RedisLimit;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.limit.redis" })
public class RedisLimitApplication {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLimitApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RedisLimitApplication.class, args);

        // 在给定的10秒里最多访问5次(超出次数返回false)；等下个10秒开始，才允许再次被访问(返回true)，周而复始
        RedisLimit redisLimit = AquariusContextAware.getBean(RedisLimit.class);
        
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.info("Timer1 - Limit={}", redisLimit.tryAccess("limit", "A-B", 10, 5));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }

            }
        }, 0L, 1000L);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.info("Timer1 - Limit={}", redisLimit.tryAccess("limit", "A-B", 10, 5));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }

            }
        }, 0L, 1500L);
    }
}