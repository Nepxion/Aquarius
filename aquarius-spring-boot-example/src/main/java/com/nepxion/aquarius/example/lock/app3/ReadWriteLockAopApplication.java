package com.nepxion.aquarius.example.lock.app3;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.example.lock.service.MyService3;
import com.nepxion.aquarius.example.lock.service.MyService4Impl;
import com.nepxion.aquarius.lock.annotation.EnableLock;

@SpringBootApplication
@EnableLock
@ComponentScan(basePackages = { "com.nepxion.aquarius.example.lock.service" })
public class ReadWriteLockAopApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ReadWriteLockAopApplication.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ReadWriteLockAopApplication.class, args);

        // 执行效果是先打印doW，即拿到写锁，再打印若干个doR，即可以同时拿到若干个读锁
        MyService4Impl myService4 = applicationContext.getBean(MyService4Impl.class);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get write lock...");
                // 写锁逻辑，最高持锁15秒，睡眠10秒，10秒后释放读锁
                myService4.doW("X", "Y");
            }
        }, 0L, 600000L);

        MyService3 myService3 = applicationContext.getBean(MyService3.class);
        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get read lock...");
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 读锁逻辑，最高持锁5秒，睡眠2秒，2秒后释放读锁
                            myService3.doR("X", "Y");
                        }
                    }).start();
                }
            }
        }, 2000L, 2000L);
    }

    // 如下方式，只支持Spring Cloud F版以前的版本
    /*@Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8089);

        return tomcatFactory;
    }*/
}