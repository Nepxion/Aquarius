package com.nepxion.aquarius.example;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.nepxion.aquarius.cache.annotation.EnableCache;
import com.nepxion.aquarius.idgenerator.annotation.EnableLocalIdGenerator;
import com.nepxion.aquarius.idgenerator.annotation.EnableRedisIdGenerator;
import com.nepxion.aquarius.idgenerator.annotation.EnableZookeeperIdGenerator;
import com.nepxion.aquarius.limit.annotation.EnableLimit;
import com.nepxion.aquarius.lock.annotation.EnableLock;

@SpringBootApplication
@EnableLock
@EnableCache
@EnableLimit
@EnableLocalIdGenerator
@EnableRedisIdGenerator
@EnableZookeeperIdGenerator
public class AquariusApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AquariusApplication.class).run(args);
    }
}