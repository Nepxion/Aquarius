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
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// @EnableDiscoveryClient
@ComponentScan(basePackages = { "com.nepxion.aquarius" })
public class AquariusApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AquariusApplication.class).web(true).run(args);
    }
}