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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.limit.context.MyContextAware4;
import com.nepxion.aquarius.limit.redis.RedisLimit;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.limit" })
public class MyApplication5 {
    private static final Logger LOG = LoggerFactory.getLogger(MyApplication5.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MyApplication5.class, args);

        RedisLimit redisLimit = MyContextAware4.getBean(RedisLimit.class);
        System.out.println(redisLimit.tryAccess("Limit-abc", 10, 5, 0, 0, true));
    }
}