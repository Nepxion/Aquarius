package com.nepxion.aquarius.idgenerator;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nepxion.aquarius.idgenerator.redis.RedisIdGenerator;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;

@RestController
public class IdGeneratorController {
    @Autowired
    private RedisIdGenerator redisIdGenerator;

    @Autowired
    private ZookeeperIdGenerator zookeeperIdGenerator;

    @RequestMapping(value = "/nextUniqueId", method = RequestMethod.GET)
    public String nextUniqueId(@RequestParam String name, @RequestParam String key, @RequestParam int step, @RequestParam int length) {
        return redisIdGenerator.nextUniqueId(name, key, step, length);
    }

    @RequestMapping(value = "/nextSequenceId", method = RequestMethod.GET)
    public int nextSequenceId(@RequestParam String name, @RequestParam String key) {
        try {
            return zookeeperIdGenerator.nextSequenceId(name, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}