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
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.cache.annotation.EnableCache;
import com.nepxion.aquarius.example.cache.service.MyService5;

@SpringBootApplication
@EnableCache
@ComponentScan(basePackages = { "com.nepxion.aquarius.example.cache.service" })
public class CacheAopApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(CacheAopApplication.class, args);

        // 下面步骤请一步步操作，然后结合Redis Desktop Manager等工具查看效果
        MyService5 myService5 = applicationContext.getBean(MyService5.class);

        // 新增缓存Key为M-N，Value为A到Redis
        myService5.doA("M", "N");

        // 新增缓存Key为P-Q，Value为A到Redis
        myService5.doA("P", "Q");

        // 更新缓存Key为M-N，Value为B到Redis
        myService5.doB("M", "N");

        // 清除缓存Key为M-N到Redis，精确匹配，因为注解上allEntries = false
        // myService5.doC("M", "N");

        // MyService6Impl myService6 = applicationContext.getBean(MyService6Impl.class);

        // 新增缓存Key为X-Y，Value为D到Redis
        // myService6.doD("X", "Y");

        // 新增缓存Key为P-Q，Value为D到Redis
        // myService6.doD("P", "Q");

        // 更新缓存Key为X-Y，Value为E到Redis
        //myService6.doE("X", "Y");

        // 清除缓存Key为X-Y到Redis，全局模糊匹配，因为注解上allEntries = true
        // myService6.doF("X", "Y");
    }

    @Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8081);

        return tomcatFactory;
    }
}