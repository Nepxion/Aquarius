# Nepxion Aquarius
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?label=license)](https://github.com/Nepxion/Aquarius/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.nepxion/aquarius.svg?label=maven%20central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.nepxion%22%20AND%20aquarius)
[![Javadocs](http://www.javadoc.io/badge/com.nepxion/aquarius.svg)](http://www.javadoc.io/doc/com.nepxion/aquarius)
[![Build Status](https://travis-ci.org/Nepxion/Aquarius.svg?branch=master)](https://travis-ci.org/Nepxion/Aquarius)

Nepxion Aquarius是一款基于Redis + Zookeeper的分布式应用组件集合，包含分布式锁，缓存，ID生成器，限速限流器。它采用Nepxion Matrix AOP框架进行切面架构，提供注解调用方式，同时也提供API调用方式

## 简介
分布式应用组件集合，包含

    1 Nepxion Aquarius Lock 分布式锁(支持Redis、Zookeeper、ReentrantLock本地锁)
    2 Nepxion Aquarius Cache 分布式缓存(支持Redis)
    3 Nepxion Aquarius ID Generator 分布式全局唯一ID(支持Redis)、全局唯一序号生成(支持Zookeeper、Twitter雪花ID算法的支持)，支持单个和批量获取
    4 Nepxion Aquarius Limit 分布式限速限流(支持Redis、本地限速限流)

    上述4大组件同时支持SpringBoot和SpringCloud部署，分别参考aquarius-spring-boot-example和aquarius-spring-cloud-example工程，文档只以aquarius-spring-boot-example为例来阐述使用方法
    支持Swagger，打开http://localhost:2222/swagger-ui.html访问

![Alt text](https://github.com/Nepxion/Aquarius/blob/master/aquarius-doc/Swagger.jpg)

### 依赖

笼统化引用
```xml
分布式锁
<dependency>
  <groupId>com.nepxion</groupId>
  <artifactId>aquarius-assembly-lock</artifactId>
  <version>${aquarius.version}</version>
</dependency>

分布式缓存
<dependency>
  <groupId>com.nepxion</groupId>
  <artifactId>aquarius-assembly-cache</artifactId>
  <version>${aquarius.version}</version>
</dependency>

分布式全局唯一ID
<dependency>
  <groupId>com.nepxion</groupId>
  <artifactId>aquarius-assembly-id-generator</artifactId>
  <version>${aquarius.version}</version>
</dependency>

分布式限速限流
<dependency>
  <groupId>com.nepxion</groupId>
  <artifactId>aquarius-assembly-limit</artifactId>
  <version>${aquarius.version}</version>
</dependency>

四个组件全集
<dependency>
  <groupId>com.nepxion</groupId>
  <artifactId>aquarius-assembly-all</artifactId>
  <version>${aquarius.version}</version>
</dependency>
```

精细化引用(只挑选跟自己感兴趣的组件)
```xml
Redis分布式锁
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-lock-redis</artifactId>
  <version>${aquarius.version}</version>
</dependency>

Zookeeper分布式锁
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-lock-zookeeper</artifactId>
  <version>${aquarius.version}</version>
</dependency>

本地锁
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-lock-local</artifactId>
  <version>${aquarius.version}</version>
</dependency>

Redis分布式缓存
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-cache-redis</artifactId>
  <version>${aquarius.version}</version>
</dependency>

Redis唯一ID产生器
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-id-generator-redis</artifactId>
  <version>${aquarius.version}</version>
</dependency>

Zookeeper唯一序号产生器
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-id-generator-zookeeper</artifactId>
  <version>${aquarius.version}</version>
</dependency>

本地唯一ID产生器
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-id-generator-local</artifactId>
  <version>${aquarius.version}</version>
</dependency>

Redis限速限流
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-limit-redis</artifactId>
  <version>${aquarius.version}</version>
</dependency>

本地限速限流
<dependency>
  <groupId>${project.groupId}</groupId>
  <artifactId>aquarius-limit-local</artifactId>
  <version>${aquarius.version}</version>
</dependency>
```

## Nepxion Aquarius Lock
基于Redisson(Redis)、Curator(Zookeeper)分布式锁和本地锁，构建于Nepxion Matrix AOP framework，你可以在这三个锁组件中选择一个移植入你的应用中

### 提示
    注解的Key支持Java8下的SPEL语义拼装。但SPEL语义对于接口代理的方式，需要打开编译参数项
    参照Nepxion Marix文档里的说明，需要在IDE和Maven里设置"-parameters"的Compiler Argument。具体参考如下：
    1)Eclipse加"-parameters"参数：https://www.concretepage.com/java/jdk-8/java-8-reflection-access-to-parameter-names-of-method-and-constructor-with-maven-gradle-and-eclipse-using-parameters-compiler-argument
    2)Idea加"-parameters"参数：http://blog.csdn.net/royal_lr/article/details/52279993

### 介绍
    1 锁既支持Redisson(基于Redis)和Curator(基于Zookeeper)机制的分布式锁，也支持ReentrantLock机制的本地锁
    2 锁既支持普通可重入锁，也支持读/写可重入锁
       2.1 普通可重入锁都是互斥的
       2.2 读/写可重入锁必须配对使用，规则如下：
       1)当写操作时，其他分布式进程/线程无法读取或写入数据；当读操作时，其他分布式进程/线程无法写入数据，但可以读取数据
       2)允许同时有多个读锁，但是最多只能有一个写锁。多个读锁不互斥，读锁与写锁互斥
    3 锁既支持公平锁，也支持非公平锁
    4 锁既支持同步执行方式，也支持异步执行方式(异步拿锁，同步阻塞)
    5 锁既支持持锁时间后丢弃，也支持持锁超时等待时间
    6 锁注解既可以加在接口上，也可以加在实现类上，也可以加在没有接口只有类的情形下
       6.1 注解说明
       1)注解com.nepxion.aquarius.lock.annotation.Lock，普通可重入锁
       2)注解com.nepxion.aquarius.lock.annotation.ReadLock，读可重入锁
       3)注解com.nepxion.aquarius.lock.annotation.WriteLock，写可重入锁
       6.2 参数说明
       1)name 锁的名字
       2)key 锁的Key。锁Key的完整路径是prefix + "_" + name + "_" + key，prefix为config.propertie里的namespace值
       3)leaseTime 持锁时间，持锁超过此时间则自动丢弃锁(Redisson支持，Curator和本地锁不支持)
       4)waitTime 没有获取到锁时，等待时间
       5)async 是否采用锁的异步执行方式(默认都支持同步执行方式，Redisson三种锁都支持异步，Curator三种锁都不支持异步，本地锁三种锁都不支持异步)
       6)fair 是否采用公平锁(默认都支持非公平锁，Redisson三种锁只有普通可重入锁支持公平锁，Curator三种锁都不支持公平锁，本地锁三种锁都支持公平锁)
    7 锁由于是可重入锁，支持缓存和重用机制
    8 锁组件采用通过改变Pom中对锁中间件类型的引用，达到快速切换分布式锁的目的
       8.1 实现对redisson支持若干种部署方式(例如单机，集群，哨兵模式)，并支持json和yaml(默认)两种配置方式，要切换部署方式，只需要修改相应的config-redisson.yaml文件即可。具体参考如下：
       https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
       8.2 实现对Curator的多种重试机制(例如exponentialBackoffRetry, boundedExponentialBackoffRetry, retryNTimes, retryForever, retryUntilElapsed)，可在配置文件里面切换
    9 锁支持两种调用方式，注解方式和直接调用方式

### 切换锁类型
aquarius-spring-boot-example\src\main\resources\application.properties，切换lockType即可
```java
# Lock config
# redisLock, zookeeperLock, localLock
lock.type=redisLock
# 指定你使用相关注解所在的接口或者类的目录
lock.scan.packages=com.nepxion.aquarius.lock
```

### 示例
使用分布式锁示例如下，更多细节见aquarius-spring-boot-example工程下com.nepxion.aquarius.lock

普通分布式锁的使用

注解方式
```java
package com.nepxion.aquarius.lock.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.aquarius.lock.annotation.Lock;

public interface MyService1 {
    @Lock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 5000L, waitTime = 60000L, async = false, fair = false)
    String doA(String id1, String id2);

    String doB(String id1, String id2);
}
```

```java
package com.nepxion.aquarius.lock.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nepxion.aquarius.lock.annotation.Lock;

@Service("myService2Impl")
public class MyService2Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService2Impl.class);

    @Lock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 5000L, waitTime = 60000L, async = false, fair = false)
    public String doC(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doC - lock is got");

        return "C";
    }

    public String doD(String id1, String id2) {
        LOG.info("doD");

        return "D";
    }
}
```

```java
package com.nepxion.aquarius.lock;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.lock.service.MyService1;
import com.nepxion.aquarius.lock.service.MyService2Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.lock" })
public class LockAopApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(LockAopApplication.class, args);

        // 执行效果是doA和doC无序打印，即谁拿到锁谁先运行
        MyService1 myService1 = AquariusContextAware.getBean(MyService1.class);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myService1.doA("X", "Y");
                }

            }).start();
        }

        MyService2Impl myService2 = AquariusContextAware.getBean(MyService2Impl.class);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myService2.doC("X", "Y");
                }

            }).start();
        }
    }
}
```

直接调用方式
```java
package com.nepxion.aquarius.lock;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.lock.entity.LockType;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.lock" })
public class LockApplication {
    private static final Logger LOG = LoggerFactory.getLogger(LockApplication.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        SpringApplication.run(LockApplication.class, args);

        LockExecutor<Object> lockExecutor = AquariusContextAware.getBean(LockExecutor.class);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Object lock = null;
                    try {
                        lock = lockExecutor.tryLock(LockType.LOCK, "lock", "X-Y", 5000L, 60000L, false, false);
                        if (lock != null) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(2000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            LOG.info("doA - lock is got");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            lockExecutor.unlock(lock);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Object lock = null;
                    try {
                        lock = lockExecutor.tryLock(LockType.LOCK, "lock", "X-Y", 5000L, 60000L, false, false);
                        if (lock != null) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(2000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            LOG.info("doC - lock is got");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            lockExecutor.unlock(lock);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}
```

读/写分布式锁的使用

注解方式
```java
package com.nepxion.aquarius.lock.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.aquarius.lock.annotation.ReadLock;

public interface MyService3 {
    @ReadLock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 5000L, waitTime = 60000L, async = false, fair = false)
    String doR(String id1, String id2);
}
```

```java
package com.nepxion.aquarius.lock.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nepxion.aquarius.lock.annotation.WriteLock;

@Service("myService4Impl")
public class MyService4Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService4Impl.class);

    @WriteLock(name = "lock", key = "#id1 + \"-\" + #id2", leaseTime = 15000L, waitTime = 60000L, async = false, fair = false)
    public String doW(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doW - write lock is got");

        return "W";
    }
}
```

```java
package com.nepxion.aquarius.lock;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
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
import com.nepxion.aquarius.lock.service.MyService3;
import com.nepxion.aquarius.lock.service.MyService4Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.lock" })
public class ReadWriteLockAopApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ReadWriteLockAopApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ReadWriteLockAopApplication.class, args);

        // 执行效果是先打印doW，即拿到写锁，再打印若干个doR，即可以同时拿到若干个读锁
        MyService4Impl myService4 = AquariusContextAware.getBean(MyService4Impl.class);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get write lock...");
                // 写锁逻辑，最高持锁15秒，睡眠10秒，10秒后释放读锁
                myService4.doW("X", "Y");
            }
        }, 0L, 600000L);

        MyService3 myService3 = AquariusContextAware.getBean(MyService3.class);
        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get read lock...");
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 读锁逻辑，最高持锁5秒，睡眠2秒，2秒后释放读锁
                            myService3.doR("X", "Y");
                        }
                    }).start();
                }
            }
        }, 2000L, 2000L);
    }
}
```

直接调用方式
```java
package com.nepxion.aquarius.lock;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.lock.entity.LockType;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.lock" })
public class ReadWriteLockApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ReadWriteLockApplication.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ReadWriteLockApplication.class, args);

        LockExecutor<Object> lockExecutor = AquariusContextAware.getBean(LockExecutor.class);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get write lock...");
                // 写锁逻辑，最高持锁15秒，睡眠10秒，10秒后释放读锁
                Object lock = null;
                try {
                    lock = lockExecutor.tryLock(LockType.WRITE_LOCK, "lock", "X-Y", 15000L, 60000L, false, false);
                    if (lock != null) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(10000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        LOG.info("doW - write lock is got");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lockExecutor.unlock(lock);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0L, 600000L);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LOG.info("Start to get read lock...");
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 读锁逻辑，最高持锁5秒，睡眠2秒，2秒后释放读锁
                            Object lock = null;
                            try {
                                lock = lockExecutor.tryLock(LockType.READ_LOCK, "lock", "X-Y", 5000L, 60000L, false, false);
                                if (lock != null) {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(2000L);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    LOG.info("doR - read lock is got");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    lockExecutor.unlock(lock);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            }
        }, 2000L, 2000L);
    }
}
```

## Nepxion Aquarius Cache
基于原生的RedisTemplate来实现(本采用Redisson的缓存模块，只在付费的Redisson PRO下才支持，故作罢)，构建于Nepxion Matrix AOP framework

### 介绍
    1 缓存注解既可以加在接口上，也可以加在实现类上，也可以加在没有接口只有类的情形下
       1.1 注解说明
       1)注解com.nepxion.aquarius.cache.annotation.Cacheable，新增缓存
       2)注解com.nepxion.aquarius.cache.annotation.CachePut，更新缓存
       3)注解com.nepxion.aquarius.cache.annotation.CacheEvict，清除缓存
       1.2 参数说明
       1)name 缓存的名字
       2)key 缓存Key。缓存Key的完整路径是prefix + "_" + name + "_" + key，prefix为config.propertie里的namespace值
       3)expire 过期时间，一旦过期，缓存数据自动会从Redis删除（只用于Cacheable和CachePut）
       4)allEntries 是否全部清除缓存内容（只用于CacheEvict）。如果为true，按照prefix + "_" + name + "*"方式去匹配删除Key；如果为false，则按照prefix + "_" + name + "_" + key + "*"
       5)beforeInvocation 缓存清理是在方法调用前还是调用后（只用于CacheEvict）
    2 缓存的Key在config-redis.xml中有个RedisCacheEntity的prefix(前缀)全局配置项目，它和name，key组成一个SPEL语义，即[prefix]_[name]_[key]，该值将作为Redis的Key存储，对应的Redis的Value就是缓存
    3 对于方法返回的值为null的时候，不做任何缓存相关操作；对于方法执行过程中抛出异常后，不做任何缓存相关操作

### 切换缓存类型
```java
# Cache config
cache.type=redisCache
cache.scan.packages=com.nepxion.aquarius.cache
```

### 示例
使用分布式缓存示例如下，更多细节见aquarius-spring-boot-example工程下com.nepxion.aquarius.cache
```java
package com.nepxion.aquarius.cache.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;

public interface MyService5 {
    @Cacheable(name = "cache", key = "#id1 + \"-\" + #id2", expire = -1L)
    String doA(String id1, String id2);

    @CachePut(name = "cache", key = "#id1 + \"-\" + #id2", expire = -1L)
    String doB(String id1, String id2);

    @CacheEvict(name = "cache", key = "#id1 + \"-\" + #id2", allEntries = false, beforeInvocation = false)
    String doC(String id1, String id2);
}
```

```java
package com.nepxion.aquarius.cache.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;

@Service("myService6Impl")
public class MyService6Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService6Impl.class);

    @Cacheable(name = "cache", key = "#id1 + \"-\" + #id2", expire = 60000L)
    public String doD(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doD");

        return "D";
    }

    @CachePut(name = "cache", key = "#id1 + \"-\" + #id2", expire = 60000L)
    public String doE(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doE");

        return "E";
    }

    @CacheEvict(name = "cache", key = "#id1 + \"-\" + #id2", allEntries = false, beforeInvocation = false)
    public String doF(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doF");

        return "F";
    }
}
```

```java
package com.nepxion.aquarius.cache;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
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
```

## Nepxion Aquarius ID Generator
### 介绍
    1 支持序号在Zookeeper上分布式生成
      1)计算对象在Zookeeper中的节点名为"/" + prefix + "/" + name + "_" + key
      2)每个分布式系统拿到的ID都是全局不重复，加1
    2 支持全局唯一ID在Redis上分布式生成
      1)计算对象在Redis中的Key名为prefix + "_" + name + "_" + key
      2)每个分布式系统拿到的ID都是全局不重复，ID规则：
        ID的前半部分为yyyyMMddHHmmssSSS格式的17位数字
        ID的后半部分为由length(最大为8位，如果length > 8，则取8)决定，取值Redis对应Value，如果小于length所对应的数位，如果不足该数位，前面补足0
        例如Redis对应Value为1234，length为8，那么ID的后半部分为00001234；length为2，那么ID的后半部分为34
    3 支持根据Twitter雪花ID本地算法，模拟分布式ID产生
      SnowFlake算法用来生成64位的ID，刚好可以用long整型存储，能够用于分布式系统中生产唯一的ID， 并且生成的ID有大致的顺序。 在这次实现中，生成的64位ID可以分成5个部分：
      0 - 41位时间戳 - 5位数据中心标识 - 5位机器标识 - 12位序列号

```java
/**
 * The class Snowflake id generator. Created by paascloud.net@gmail.com
 * Twitter雪花ID算法
 * 概述
 * - SnowFlake算法是Twitter设计的一个可以在分布式系统中生成唯一的ID的算法，它可以满足Twitter每秒上万条消息ID分配的请求，这些消息ID是唯一的且有大致的递增顺序
 * 
 * 原理
 * - SnowFlake算法产生的ID是一个64位的整型，结构如下（每一部分用“-”符号分隔）：
 *    0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * - 1位标识部分，在java中由于long的最高位是符号位，正数是0，负数是1，一般生成的ID为正数，所以为0
 * - 41位时间戳部分，这个是毫秒级的时间，一般实现上不会存储当前的时间戳，而是时间戳的差值（当前时间-固定的开始时间），这样可以使产生的ID从更小值开始；41位的时间戳可以使用69年，(1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69年
 * - 10位节点部分，Twitter实现中使用前5位作为数据中心标识，后5位作为机器标识，可以部署1024个节点
 * - 12位序列号部分，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间戳)产生4096个ID序号，加起来刚好64位，为一个Long型
 *  
 * 优点
 * - SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右
 * 
 * 使用
 * - SnowFlake算法生成的ID大致上是按照时间递增的，用在分布式系统中时，需要注意数据中心标识和机器标识必须唯一，这样就能保证每个节点生成的ID都是唯一的。
 *   或许我们不一定都需要像上面那样使用5位作为数据中心标识，5位作为机器标识，可以根据我们业务的需要，灵活分配节点部分，如：若不需要数据中心，完全可以使用全部10位作为机器标识；若数据中心不多，也可以只使用3位作为数据中心，7位作为机器标识
 */
```

### 示例
使用ID Generator示例如下，更多细节见aquarius-spring-boot-example工程下com.nepxion.aquarius.idgenerator
```java
package com.nepxion.aquarius.idgenerator;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
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
import com.nepxion.aquarius.idgenerator.redis.RedisIdGenerator;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.idgenerator.redis" })
public class RedisIdGeneratorApplication {
    private static final Logger LOG = LoggerFactory.getLogger(RedisIdGeneratorApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RedisIdGeneratorApplication.class, args);

        RedisIdGenerator redisIdGenerator = AquariusContextAware.getBean(RedisIdGenerator.class);

        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.info("Timer1 - Unique id={}", redisIdGenerator.nextUniqueId("idgenerater", "X-Y", 1, 8));
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
                                LOG.info("Timer2 - Unique id={}", redisIdGenerator.nextUniqueId("idgenerater", "X-Y", 1, 8));
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
                                String[] ids = redisIdGenerator.nextUniqueIds("idgenerater", "X-Y", 1, 8, 10);
                                for (String id : ids) {
                                    LOG.info("Timer3 - Unique id={}", id);
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
```

```java
package com.nepxion.aquarius.idgenerator;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
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
```

```java
package com.nepxion.aquarius.idgenerator;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
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
import com.nepxion.aquarius.idgenerator.local.LocalIdGenerator;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.idgenerator.local" })
public class LocalIdGeneratorApplication {
    private static final Logger LOG = LoggerFactory.getLogger(LocalIdGeneratorApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LocalIdGeneratorApplication.class, args);

        LocalIdGenerator localIdGenerator = AquariusContextAware.getBean(LocalIdGenerator.class);

        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.info("Timer1 - Unique id={}", localIdGenerator.nextUniqueId(2, 3));
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
                                LOG.info("Timer2 - Unique id={}", localIdGenerator.nextUniqueId(2, 3));
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
                                String[] ids = localIdGenerator.nextUniqueIds(2, 3, 10);
                                for (String id : ids) {
                                    LOG.info("Timer3 - Unique id={}", id);
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
```

## Nepxion Aquarius Limit
### 介绍
    1 支持若干个分布式系统对同一资源在给定的时间段里最多的访问限制次数(超出次数返回false)；等下个时间段开始，才允许再次被访问(返回true)，周而复始；也支持本地多线程访问的限流
    2 支持两种调用方式，注解方式和直接调用
    3 参数说明
      1)name 资源的名字
      2)key  资源Key。资源Key的完整路径是prefix + "_" + name + "_" + key，prefix为config.propertie里的namespace值
      3)limitPeriod 给定的时间段(单位为秒)
      4)limitCount 最多的访问限制次数

### 切换限流限速器类型
aquarius-spring-boot-example\src\main\resources\application.properties，切换limitType即可
```java
# Limit config
# redisLimit, localLimit
limit.type=redisLimit
limit.scan.packages=com.nepxion.aquarius.limit
```

### 示例
使用Limit示例如下，更多细节见aquarius-spring-boot-example工程下com.nepxion.aquarius.limit

注解方式
```java
package com.nepxion.aquarius.limit.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.aquarius.limit.annotation.Limit;

public interface MyService7 {
    @Limit(name = "limit", key = "#id1 + \"-\" + #id2", limitPeriod = 10, limitCount = 5)
    String doA(String id1, String id2);
}
```

```java
package com.nepxion.aquarius.limit.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nepxion.aquarius.limit.annotation.Limit;

@Service("myService8Impl")
public class MyService8Impl {
    private static final Logger LOG = LoggerFactory.getLogger(MyService8Impl.class);

    @Limit(name = "limit", key = "#id1 + \"-\" + #id2", limitPeriod = 10, limitCount = 5)
    public String doB(String id1, String id2) {
        LOG.info("doB");

        return "B";
    }
}
```

```java
package com.nepxion.aquarius.limit;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.limit.service.MyService7;
import com.nepxion.aquarius.limit.service.MyService8Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.limit" })
public class RedisLimitAopApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(RedisLimitAopApplication.class, args);

        MyService7 myService7 = AquariusContextAware.getBean(MyService7.class);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myService7.doA("X", "Y");
                    }

                }).start();
            }
        }, 0L, 3000L);

        MyService8Impl myService8 = AquariusContextAware.getBean(MyService8Impl.class);
        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myService8.doB("X", "Y");
                    }

                }).start();
            }
        }, 0L, 4000L);
    }
}
```

直接调用方式
```java
package com.nepxion.aquarius.limit;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
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
import com.nepxion.aquarius.limit.redis.RedisLimit;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.limit.redis" })
public class RedisLimitApplication {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLimitApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RedisLimitApplication.class, args);

        // 在给定的10秒里最多访问5次(超出次数返回false)；等下个10秒开始，才允许再次被访问(返回true)，周而复始
        RedisLimit redisLimit = AquariusContextAware.getBean(RedisLimit.class);
        
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.info("Timer1 - Limit={}", redisLimit.tryAccess("limit", "A-B", 10, 5));
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
                                LOG.info("Timer1 - Limit={}", redisLimit.tryAccess("limit", "A-B", 10, 5));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }

            }
        }, 0L, 1500L);
    }
}
```

## Nepxion Aquarius Spring Cloud的使用方式
### 介绍
    1 配置好Euraka服务器，aquarius-spring-cloud-example/src/main/resources/application.properties里面，修改成你本地的Eureka环境
      eureka.client.serviceUrl.defaultZone=http://cluster-1:1111/eureka/,http://cluster-2:1112/eureka/,http://cluster-3:1113/eureka/
    2 启动AquariusApplication
    3 打开Postman，或者浏览器，执行Get操作，参考下面的URL
    4 支持Swagger，打开http://localhost:2222/swagger-ui.html访问

```java
Lock
# 注解方式
http://localhost:2222/doC?id1=X&id2=Y
# 直接调用方式
http://localhost:2222/tryLock?lockType=WriteLock&name=lock&key=X-Y&leaseTime=5000&waitTime=60000&async=false&fair=false

Cache
# 注解方式
http://localhost:2222/doD?id1=X&id2=Y

Limit
# 注解方式
http://localhost:2222/doG?id1=X&id2=Y
# 直接调用方式
http://localhost:2222/tryAccess?name=limit&key=A-B&limitPeriod=10&limitCount=5

ID Generator
# 直接调用方式(Redis)
http://localhost:2222/nextUniqueId?name=idgenerater&key=X-Y&step=1&length=8

# 直接调用方式(Zookeeper)
http://localhost:2222/nextSequenceId?name=idgenerater&key=X-Y

# 直接调用方式(雪花算法)
http://localhost:2222/nextLocalUniqueId?dataCenterId=2&machineId=3
```