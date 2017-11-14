# Nepxion Aquarius
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

## 分布式应用组件集合
    1. Nepxion Aquarius Lock 分布式锁
    2. Nepxion Aquarius Cache 分布式缓存
    3. Nepxion Aquarius ID Generator 分布式全局唯一ID生成

## Nepxion Aquarius Lock
基于Redisson(Redis)、Curator(Zookeeper)分布式锁和本地锁，构建于Nepxion Matrix AOP framework，你可以在这三个锁组件中选择一个移植入你的应用中

### 介绍
    1. 锁既支持Redisson(基于Redis)和Curator(基于Zookeeper)机制的分布式锁，也支持ReentrantLock机制的本地锁
    2. 锁既支持普通可重入锁，也支持读/写可重入锁
       2.1 普通可重入锁都是互斥的
       2.2 读/写可重入锁必须配对使用，规则如下：
       1)当写操作时，其他分布式进程/线程无法读取或写入数据；当读操作时，其他分布式进程/线程无法写入数据，但可以读取数据
       2)允许同时有多个读锁，但是最多只能有一个写锁。多个读锁不互斥，读锁与写锁互斥
    3. 锁既支持公平锁，也支持非公平锁
    4. 锁既支持同步执行方式，也支持异步执行方式
    5. 锁既支持持锁时间后丢弃，也支持持锁超时等待时间
    6. 锁注解既可以加在接口上，也可以加在实现类上，也可以加在没有接口只有类的情形下
       6.1 注解说明
       1)注解com.nepxion.aquarius.lock.annotation.Lock，普通可重入锁
       2)注解com.nepxion.aquarius.lock.annotation.ReadLock，读可重入锁
       3)注解com.nepxion.aquarius.lock.annotation.WriteLock，写可重入锁
       6.2 参数说明
       1)key 锁的Key
       2)leaseTime 持锁时间，持锁超过此时间则自动丢弃锁(Redisson支持，Curator不支持，本地锁不支持)
       3)waitTime 没有获取到锁时，等待时间
       4)async 是否采用锁的异步执行方式(默认都支持同步执行方式，Redisson三种锁都支持异步，Curator三种锁都不支持异步，本地锁三种锁都不支持异步)
       5)fair 是否采用公平锁(默认都支持非公平锁，Redisson三种锁只有普通可重入锁支持公平锁，Curator三种锁都不支持公平锁，本地锁三种锁都支持公平锁)
    7. 锁由于是可重入锁，支持缓存和重用机制
    8. 锁组件采用通过改变Pom中对锁中间件类型的引用，达到快速切换分布式锁的目的
       8.1 实现对redisson支持若干种部署方式(例如单机，集群，哨兵模式)，并支持json和yaml(默认)两种配置方式，要切换部署方式，只需要修改相应的config-redisson.yaml文件即可。具体参考如下：
       https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
       8.2 实现对Curator的多种重试机制(例如exponentialBackoffRetry, boundedExponentialBackoffRetry, retryNTimes, retryForever, retryUntilElapsed)，可在配置文件里面切换
    9. 锁的Key支持SPEL语义拼装。但SPEL语义对于接口代理的方式，需要打开编译参数项
       参照Nepxion Marix文档里的说明，需要在IDE和Maven里设置"-parameters"的Compiler Argument。具体参考如下：
       https://www.concretepage.com/java/jdk-8/java-8-reflection-access-to-parameter-names-of-method-and-constructor-with-maven-gradle-and-eclipse-using-parameters-compiler-argument

### 快速切换分布式锁组件
参考aquarius-test下的pom.xml
```java
<!-- Use only one of aquarius-lock-redis, aquarius-lock-zookeeper, aquarius-lock-local -->
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>aquarius-lock-redis</artifactId>
</dependency>

<!-- <dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>aquarius-lock-zookeeper</artifactId>
</dependency>

<dependency>
    <groupId>${project.groupId}</groupId>
        <artifactId>aquarius-lock-local</artifactId>
    </dependency> -->
```

### 使用分布式锁示例如下，更多细节见aquarius-test工程下com.nepxion.aquarius.lock.test
普通分布式锁的使用
```java
package com.nepxion.aquarius.lock.test.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.lock.annotation.Lock;

public interface MyService1 {
    @Lock(key = "#id1 + \"-\" + #id2", leaseTime = 5000, waitTime = 60000, async=false, fair=false)
    void doA(String id1, String id2);

    void doB(String id1, String id2);
}
```

读/写分布式锁的使用
```java
package com.nepxion.aquarius.lock.test.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.lock.annotation.ReadLock;

public interface MyService3 {
    @ReadLock(key = "#id1 + \"-\" + #id2", leaseTime = 5000, waitTime = 60000, async=false, fair = false)
    void doR(String id1, String id2);
}
```

```java
package com.nepxion.aquarius.lock.test.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
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

    @WriteLock(key = "#id1 + \"-\" + #id2", leaseTime = 15000, waitTime = 60000, async = false, fair = false)
    public void doW(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doW");
    }
}
```

## Nepxion Aquarius Cache
基于原生的RedisTemplate来实现(本采用Redisson的缓存模块，只在付费的Redisson PRO下才支持，故作罢)，构建于Nepxion Matrix AOP framework

### 介绍
    1. 缓存注解既可以加在接口上，也可以加在实现类上，也可以加在没有接口只有类的情形下
       1.1 注解说明
       1)注解com.nepxion.aquarius.cache.annotation.Cacheable，新增缓存
       2)注解com.nepxion.aquarius.cache.annotation.CachePut，更新缓存
       3)注解com.nepxion.aquarius.cache.annotation.CacheEvict，清除缓存
       1.2 参数说明
       1)value 缓存的名字
       2)key 缓存Key
       3)expire 过期时间，一旦过期，缓存数据自动会从Redis删除（只用于Cacheable和CachePut）
       4)allEntries 是否全部清除缓存内容（只用于CacheEvict）。如果为true，按照prefix + "_" + value + "*"方式去匹配删除Key；如果为false，则按照prefix + "_" + value + "_" + key + "*"
       5)beforeInvocation 缓存清理是在方法调用前还是调用后（只用于CacheEvict）
    2. 缓存的Key支持SPEL语义拼装。但SPEL语义对于接口代理的方式，需要打开编译参数项
       参照Nepxion Marix文档里的说明，需要在IDE和Maven里设置"-parameters"的Compiler Argument。具体参考如下：
       https://www.concretepage.com/java/jdk-8/java-8-reflection-access-to-parameter-names-of-method-and-constructor-with-maven-gradle-and-eclipse-using-parameters-compiler-argument
       在config-redis.xml中有个RedisCacheEntity的prefix(前缀)全局配置项目，它和value，key组成一个SPEL语义，即[prefix]_[value]_[key]，该值将作为Redis的Key存储，对应的Redis的Value就是缓存
    3. 对于方法返回的值为null的时候，不做任何缓存相关操作；对于方法执行过程中抛出异常后，不做任何缓存相关操作

### 使用分布缓存示例如下，更多细节见aquarius-test工程下com.nepxion.aquarius.cache.test
```java
package com.nepxion.aquarius.cache.service;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;

public interface MyService5 {
    @Cacheable(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = -1L)
    String doA(String id1, String id2);

    @CachePut(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = -1L)
    String doB(String id1, String id2);

    @CacheEvict(value = "aquarius", key = "#id1 + \"-\" + #id2", allEntries = false, beforeInvocation = false)
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
 * @email 1394997@qq.com
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

    @Cacheable(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = 60000L)
    public String doD(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doD");

        return "D";
    }

    @CachePut(value = "aquarius", key = "#id1 + \"-\" + #id2", expire = 60000L)
    public String doE(String id1, String id2) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("doE");

        return "E";
    }

    @CacheEvict(value = "aquarius", key = "#id1 + \"-\" + #id2", allEntries = false, beforeInvocation = false)
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
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.nepxion.aquarius.cache.context.MyContextAware2;
import com.nepxion.aquarius.cache.service.MyService5;
import com.nepxion.aquarius.cache.service.MyService6Impl;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.nepxion.aquarius.cache" })
public class MyApplication3 {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MyApplication3.class, args);

        // 下面步骤请一步步操作，然后结合Redis Desktop Manager等工具查看效果
        MyService5 myService5 = MyContextAware2.getBean(MyService5.class);

        // 新增缓存Key为M-N，Value为A到Redis
        myService5.doA("M", "N");

        // 更新缓存Key为M-N，Value为B到Redis
        // myService5.doB("M", "N");

        // 清除缓存Key为M-N到Redis
        // myService5.doC("M", "N");

        MyService6Impl myService6 = MyContextAware2.getBean(MyService6Impl.class);

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
在路上...