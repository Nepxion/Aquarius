package com.nepxion.aquarius.idgenerator;

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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.idgenerator.zookeeper" })
public class ZookeeperIdGeneratorApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperIdGeneratorApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ZookeeperIdGeneratorApplication.class, args);

        ZookeeperIdGenerator zookeeperIdGenerator = AquariusContextAware.getBean(ZookeeperIdGenerator.class);

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
}