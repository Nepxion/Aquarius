# Nepxion Aquarius
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Nepxion Aquarius Lock
基于Redisson(Redis)、Curator(Zookeeper)分布式锁和本地锁，构建于Nepxion Matrix AOP framework，你可以在这三个锁组件中选择一个移植入你的应用中

### 介绍
    1. 锁既支持Redisson机制，也支持Curator机制的分布式锁，也支持ReentrantLock机制的本地锁
    2. 锁即支持普通可重入锁，也支持读/写可重入锁
       读/写可重入锁必须配对适用，加锁后的使用
       当写操作时，其他分布式进程/线程无法读取或写入数据；当读操作时，其他分布式进程/线程无法写入数据，但可以读取数据
       允许同时有多个读锁，但是最多只能有一个写锁。多个读锁不互斥，读锁与写锁互斥
    3. 锁既支持公平锁，也支持非公平锁
    4. 锁既支持同步执行方式，也支持异步执行方式
    5. 锁既支持持锁时间，也支持持锁超时等待时间
    6. 锁注解既可以加在接口上，也可以加在实现类上，也可以加在没有接口只有类的情形下
       注解说明
       1)注解com.nepxion.aquarius.lock.annotation.Lock，普通可重入锁
       2)注解com.nepxion.aquarius.lock.annotation.ReadLock，读可重入锁
       3)注解com.nepxion.aquarius.lock.annotation.WriteLock，写可重入锁
       参数说明
       1)key 锁的Key
       2)leaseTime 持锁时间，持锁超过此时间则自动丢弃锁(Redisson支持，Curator不支持，本地锁不支持)
       3)waitTime 没有获取到锁时，等待时间
       4)async 是否采用锁的异步执行方式(默认都支持同步执行方式，Redisson三种锁都支持异步，Curator三种锁都不支持异步，本地锁三种锁都不支持异步)
       5)fair 是否采用公平锁(默认都支持非公平锁，Redisson三种锁只有普通可重入锁支持公平锁，Curator三种锁都不支持公平锁，本地锁三种锁都支持公平锁)
    7. 锁由于是可重入锁，支持缓存和重用机制
    8. 锁组件采用SPI方式，通过配置快速切换分布式锁采用的锁中间件类型
       8.1 实现对redisson支持若干种部署方式(例如单机，集群，哨兵模式)，并支持json和yaml(默认)两种配置方式，要切换部署方式，只需要修改相应的config-redisson.yaml文件即可。具体参考如下：
       https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
       8.2 实现对Curator的多种重试机制(例如exponentialBackoffRetry, boundedExponentialBackoffRetry, retryNTimes, retryForever, retryUntilElapsed)，可在配置文件里面切换
    9. 锁的Key支持SPEL语义拼装。但SPEL语义对于接口代理的方式，需要打开编译参数项
       参照Nepxion Marix文档里的说明，需要在IDE和Maven里设置"-parameters"的Compiler Argument。具体参考如下：
       https://www.concretepage.com/java/jdk-8/java-8-reflection-access-to-parameter-names-of-method-and-constructor-with-maven-gradle-and-eclipse-using-parameters-compiler-argument

### 快速切换分布式锁组件
参考aquarius-test下的config.properties
```java
# Lock spi config
lockSpi=com.nepxion.aquarius.lock.redis.spi.RedisLockSpi
# lockSpi=com.nepxion.aquarius.lock.zookeeper.spi.ZookeeperLockSpi
# lockSpi=com.nepxion.aquarius.lock.local.spi.LocalLockSpi
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