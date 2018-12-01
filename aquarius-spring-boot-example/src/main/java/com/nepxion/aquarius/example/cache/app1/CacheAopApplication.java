package com.nepxion.aquarius.example.cache.app1;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.cache.annotation.EnableCache;
import com.nepxion.aquarius.example.cache.service.MyService5;
import com.nepxion.aquarius.example.cache.service.MyService6Impl;

@SpringBootApplication
@EnableCache
@ComponentScan(basePackages = { "com.nepxion.aquarius.example.cache.service" })
public class CacheAopApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(CacheAopApplication.class, args);

        // 下面步骤请一步步操作，然后结合Redis Desktop Manager等工具查看效果
        MyService5 myService5 = applicationContext.getBean(MyService5.class);

        // 新增缓存Key为1-1，Value为A到Redis，不过期
        myService5.doA("1", "1");

        // 新增缓存Key为2-2，Value为A到Redis，不过期
        myService5.doA("2", "2");

        // 更新缓存Key为1-1，Value为B到Redis，过期时间1分钟
        myService5.doB("1", "1");

        // 清除缓存Key为2-2到Redis，精确匹配，因为注解上allEntries = false
        myService5.doC("2", "2");

        MyService6Impl myService6 = applicationContext.getBean(MyService6Impl.class);

        // 新增缓存Key为3-3，Value为D到Redis，不过期
        myService6.doD("3", "3");

        // 新增缓存Key为4-4，Value为D到Redis，不过期
        myService6.doD("4", "4");

        // 更新缓存Key为3-3，Value为E到Redis，过期时间1分钟
        myService6.doE("3", "3");

        // 清除缓存Key为4-4到Redis，全局模糊匹配，因为注解上allEntries = true
        myService6.doF("4", "4");
    }

    // 如下方式，只支持Spring Cloud F版以前的版本
    /*@Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8081);

        return tomcatFactory;
    }*/
}