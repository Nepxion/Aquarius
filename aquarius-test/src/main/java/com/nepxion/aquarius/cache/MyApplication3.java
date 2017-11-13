package com.nepxion.aquarius.cache;

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

import com.nepxion.aquarius.cache.context.MyContextAware2;
import com.nepxion.aquarius.cache.service.MyService5;
import com.nepxion.aquarius.cache.service.MyService6Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.common.redis.config", "com.nepxion.aquarius.cache" })
public class MyApplication3 {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MyApplication3.class, args);

        MyService5 myService5 = MyContextAware2.getBean(MyService5.class);
        myService5.doA("A1", "A2");
        myService5.doB("B1", "B2");
        myService5.doC("C1", "C2");

        MyService6Impl myService6 = MyContextAware2.getBean(MyService6Impl.class);
        myService6.doD("D1", "D2");
        myService6.doE("E1", "E2");
        myService6.doF("F1", "F2");
    }
}