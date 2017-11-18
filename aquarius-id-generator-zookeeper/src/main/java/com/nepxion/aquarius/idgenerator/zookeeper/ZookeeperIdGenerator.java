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
     * 获取序号
     * @param name 资源名字
     * @param key 资源Key
     * @return
     * @throws Exception
     */
    int nextSequenceId(String name, String key) throws Exception;

    int nextSequenceId(String compositeKey) throws Exception;
}