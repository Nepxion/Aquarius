package com.nepxion.aquarius.example.lock.app1;

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

import com.nepxion.aquarius.example.lock.service.MyService1;
import com.nepxion.aquarius.example.lock.service.MyService2Impl;
import com.nepxion.aquarius.lock.annotation.EnableLock;

@SpringBootApplication
@EnableLock
@ComponentScan(basePackages = { "com.nepxion.aquarius.example.lock.service" })
public class LockAopApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(LockAopApplication.class, args);

        // 执行效果是doA和doC无序打印，即谁拿到锁谁先运行
        MyService1 myService1 = applicationContext.getBean(MyService1.class);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myService1.doA("X", "Y");
                }
            }).start();
        }

        MyService2Impl myService2 = applicationContext.getBean(MyService2Impl.class);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myService2.doC("X", "Y");
                }
            }).start();
        }
    }

    // 如下方式，只支持Spring Cloud F版以前的版本
    /*@Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8087);

        return tomcatFactory;
    }*/
}