package com.nepxion.aquarius.idgenerator.local;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

public interface LocalIdGenerator {
    /**
     * 获取全局唯一ID，根据Twitter雪花ID算法
     * SnowFlake算法用来生成64位的ID，刚好可以用long整型存储，能够用于分布式系统中生产唯一的ID， 并且生成的ID有大致的顺序。 在这次实现中，生成的64位ID可以分成5个部分：
     * 0 - 41位时间戳 - 5位数据中心标识 - 5位机器标识 - 12位序列号
     * @param workerId 机器ID
     * @param dataCenterId 数据中心ID
     * @return
     */
    long nextUniqueId(long workerId, long dataCenterId);
}