package com.nepxion.aquarius.idgenerator.zookeeper;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

public interface ZookeeperIdGenerator {
    /**
     * 获取全局唯一序号
     * @param name 资源名字
     * @param key 资源Key
     * @return
     * @throws Exception
     */
    String nextSequenceId(String name, String key) throws Exception;

    String nextSequenceId(String compositeKey) throws Exception;

    String[] nextSequenceIds(String name, String key, int count) throws Exception;

    String[] nextSequenceIds(String compositeKey, int count) throws Exception;
}