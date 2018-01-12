package com.nepxion.aquarius.cache;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.cache.service.MyService5;
import com.nepxion.aquarius.cache.service.MyService6Impl;
import com.nepxion.aquarius.common.context.AquariusContextAware;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.cache" })
public class CacheAopApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CacheAopApplication.class, args);

        // 下面步骤请一步步操作，然后结合Redis Desktop Manager等工具查看效果
        MyService5 myService5 = AquariusContextAware.getBean(MyService5.class);

        // 新增缓存Key为M-N，Value为A到Redis
        myService5.doA("M", "N");

        // 更新缓存Key为M-N，Value为B到Redis
        // myService5.doB("M", "N");

        // 清除缓存Key为M-N到Redis
        // myService5.doC("M", "N");

        MyService6Impl myService6 = AquariusContextAware.getBean(MyService6Impl.class);

        // 新增缓存Key为X-Y，Value为D到Redis
        myService6.doD("X", "Y");

        // 更新缓存Key为X-Y，Value为E到Redis
        //myService6.doE("X", "Y");

        // 清除缓存Key为X-Y到Redis
        // myService6.doF("X", "Y");
    }
}