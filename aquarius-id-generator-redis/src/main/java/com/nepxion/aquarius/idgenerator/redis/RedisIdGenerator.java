package com.nepxion.aquarius.idgenerator.redis;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.List;

public interface RedisIdGenerator {
    /**
     * 获取全局唯一ID
     * @param name 资源名字
     * @param key 资源Key。在Redis中的Key名为prefix + "_" + name + "_" + key
     * @return
     * @throws Exception
     */
    String nextUniqueId(String name, String key);

    /**
     * 批量获取全局唯一ID
     * @param name 资源名字
     * @param key 资源Key。在Redis中的Key名为prefix + "_" + name + "_" + key
     * @return
     * @throws Exception
     */
    List<String> nextUniqueIds(String name, String key);
}