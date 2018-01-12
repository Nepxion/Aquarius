package com.nepxion.aquarius.limit;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.limit.service.MyService7;
import com.nepxion.aquarius.limit.service.MyService8Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.limit" })
public class RedisLimitAopApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(RedisLimitAopApplication.class, args);

        MyService7 myService7 = AquariusContextAware.getBean(MyService7.class);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myService7.doA("X", "Y");
                    }

                }).start();
            }
        }, 0L, 3000L);

        MyService8Impl myService8 = AquariusContextAware.getBean(MyService8Impl.class);
        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myService8.doB("X", "Y");
                    }

                }).start();
            }
        }, 0L, 4000L);
    }
}