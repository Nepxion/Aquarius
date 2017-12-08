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
    long nextUniqueId(long dataCenterId, long machineId);

    long nextUniqueId(String startTimestamp, long dataCenterId, long machineId) throws Exception;

    /**
     * 获取全局唯一ID，根据Twitter雪花ID算法
     * SnowFlake算法用来生成64位的ID，刚好可以用long整型存储，能够用于分布式系统中生产唯一的ID， 并且生成的ID有大致的顺序。 在这次实现中，生成的64位ID可以分成5个部分：
     * 0 - 41位时间戳 - 5位数据中心标识 - 5位机器标识 - 12位序列号
     * @param startTimestamp 起始计算时间戳(默认2017-01-01)
     * @param dataCenterId 数据中心标识ID
     * @param machineId 机器标识ID
     * @return
     */
    long nextUniqueId(long startTimestamp, long dataCenterId, long machineId);
}