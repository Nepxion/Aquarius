package com.nepxion.aquarius.lock.test;

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

import com.nepxion.aquarius.lock.test.context.MyContextAware;
import com.nepxion.aquarius.lock.test.service.MyService3;
import com.nepxion.aquarius.lock.test.service.MyService4Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.lock" })
public class MyApplication2 {
    private static final Logger LOG = LoggerFactory.getLogger(MyApplication2.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MyApplication2.class, args);

        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get write lock...");
                // 写锁逻辑，最高15秒，睡眠10秒，10秒后释放读锁
                MyService4Impl myService4 = MyContextAware.getBean(MyService4Impl.class);
                myService4.doW("X", "Y");
            }
        }, 0, 600000);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get read lock...");
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 读锁逻辑，最高持锁5秒，睡眠2秒，2秒后释放读锁
                            MyService3 myService3 = MyContextAware.getBean(MyService3.class);
                            myService3.doR("X", "Y");
                        }

                    }).start();
                }
            }
        }, 2000, 2000);
    }
}