# Nepxion Aquarius
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Nepxion Aquarius Lock
基于Redisson(Redis)和Curator(Zookeeper)，构建于Nepxion Matrix AOP framework，可以二选一轻松快速实现注解式的分布式锁

### 介绍
    1. 锁注解既可以加在接口上，也可以加在实现类上，也可以加在没有接口只有类的情形下
    2. 锁既支持Redisson机制，也支持Curator机制
    3. 采用SPI方式，可以通过改变pom对aquarius-lock-redis或者aquarius-lock-zookeeper的引用，快速切换分布式锁采用的中间件类型
    4. 支持SPEL语义实现分布式锁Key的拼装
       参照Nepxion Marix文档里的说明，需要在IDE和Maven里设置"-parameters"的Compiler Argument。具体参考如下：
       https://www.concretepage.com/java/jdk-8/java-8-reflection-access-to-parameter-names-of-method-and-constructor-with-maven-gradle-and-eclipse-using-parameters-compiler-argument
    5. 对Redisson分布式锁的说明
       5.1 实现对redisson支持若干种部署方式(例如单机，集群，哨兵模式)，并支持json和yaml(默认)两种配置方式，要切换部署方式，只需要修改相应的config-redisson.yaml文件即可。具体参考如下：
       https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
       5.2 实现对redisson多种类型锁的支持
           1)普通可重入锁，对应的注解是com.nepxion.aquarius.lock.annotation.Lock
           2)普通可重入锁(采用锁的异步执行方式)，对应的注解是com.nepxion.aquarius.lock.annotation.Lock，属性async改成true即可，默认为false
           3)公平可重入锁，对应的注解是com.nepxion.aquarius.lock.annotation.Lock，属性fair改成true即可，默认为false
           4)公平可重入锁(采用锁的异步执行方式)，对应的注解是com.nepxion.aquarius.lock.annotation.Lock，属性fair改成true即可，默认为false，属性async改成true即可，默认为false
           5)读/写可重入锁，包括
           5.1)读可重入锁，对应的注解是com.nepxion.aquarius.lock.annotation.ReadLock
           5.2)读可重入锁(采用锁的异步执行方式)，对应的注解是com.nepxion.aquarius.lock.annotation.ReadLock，属性async改成true即可，默认为false
           5.3)写可重入锁，对应的注解是com.nepxion.aquarius.lock.annotation.WriteLock
           5.4)写可重入锁(采用锁的异步执行方式)，对应的注解是com.nepxion.aquarius.lock.annotation.WriteLock，属性async改成true即可，默认为false
           读/写可重入锁必须配对适用，加锁后的使用结果如下：
           当写操作时，其他分布式进程/线程无法读取或写入数据；当读操作时，其他分布式进程/线程无法写入数据，但可以读取数据
           允许同时有多个读锁，但是最多只能有一个写锁。多个读锁不互斥，读锁与写锁互斥
    6. 对Curator分布式锁的说明，功能没Redission强大
       6.1 实现对curator的多种重试机制(例如exponentialBackoffRetry, boundedExponentialBackoffRetry, retryNTimes, retryForever, retryUntilElapsed)，可在配置文件里面切换
       6.2 实现对curator多种类型锁的支持
           1)普通可重入锁(不支持锁的异步执行方式)，对应的注解是com.nepxion.aquarius.lock.annotation.Lock
           2)读/写可重入锁，包括
           2.1)读可重入锁(不支持锁的异步执行方式)，对应的注解是com.nepxion.aquarius.lock.annotation.ReadLock
           2.2)写可重入锁(不支持锁的异步执行方式)，对应的注解是com.nepxion.aquarius.lock.annotation.WriteLock
           读/写可重入锁原理和规则跟Redission一样

### 快速切换分布式锁组件
lock-test下的pom.xml为例子，redis和zookeeper二选一
```java
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>aquarius-lock-redis</artifactId>
</dependency>

<!-- <dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>aquarius-lock-zookeeper</artifactId>
</dependency> -->
```

### 使用分布式锁示例，见aquarius-test工程下com.nepxion.aquarius.lock.test	   
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
    @ReadLock(key = "#id1 + \"-\" + #id2", leaseTime = 5000, waitTime = 60000, async=false)
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

    @WriteLock(key = "#id1 + \"-\" + #id2", leaseTime = 15000, waitTime = 60000, async = false)
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