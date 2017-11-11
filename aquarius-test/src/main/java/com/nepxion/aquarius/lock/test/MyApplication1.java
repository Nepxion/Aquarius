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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.lock.test.context.MyContextAware;
import com.nepxion.aquarius.lock.test.service.MyService1;
import com.nepxion.aquarius.lock.test.service.MyService2Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.lock" })
public class MyApplication1 {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MyApplication1.class, args);

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyService1 myService1 = MyContextAware.getBean(MyService1.class);
                    myService1.doA("X", "Y");
                }

            }).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyService2Impl myService2 = MyContextAware.getBean(MyService2Impl.class);
                    myService2.doC("X", "Y");
                }

            }).start();
        }
    }
}