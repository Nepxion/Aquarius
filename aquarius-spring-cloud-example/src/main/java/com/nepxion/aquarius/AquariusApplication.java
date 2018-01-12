package com.nepxion.aquarius;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
// @EnableDiscoveryClient
@EnableAutoConfiguration
public class AquariusApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AquariusApplication.class).web(true).run(args);
    }
}