package com.nepxion.aquarius.example.idgenerator.app3;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.nepxion.aquarius.idgenerator.annotation.EnableZookeeperIdGenerator;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;

@SpringBootApplication
@EnableZookeeperIdGenerator
public class ZookeeperIdGeneratorApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperIdGeneratorApplication.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ZookeeperIdGeneratorApplication.class, args);

        ZookeeperIdGenerator zookeeperIdGenerator = applicationContext.getBean(ZookeeperIdGenerator.class);

        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.info("Timer1 - Sequence id={}", zookeeperIdGenerator.nextSequenceId("idgenerater", "X-Y"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }, 0L, 1000L);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.info("Timer2 - Sequence id={}", zookeeperIdGenerator.nextSequenceId("idgenerater", "X-Y"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }, 0L, 1500L);

        Timer timer3 = new Timer();
        timer3.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String[] ids = zookeeperIdGenerator.nextSequenceIds("idgenerater", "X-Y", 10);
                                for (String id : ids) {
                                    LOG.info("Timer3 - Sequence id={}", id);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }, 0L, 3000L);
    }

    // 如下方式，只支持Spring Cloud F版以前的版本
    /*@Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8084);

        return tomcatFactory;
    }*/
}