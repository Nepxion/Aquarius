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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nepxion.aquarius.idgenerator.redis.RedisIdGenerator;
import com.nepxion.aquarius.idgenerator.zookeeper.ZookeeperIdGenerator;

@RestController
@Api(tags = { "分布式ID和序号生成器接口" })
public class IdGeneratorController {
    @Autowired
    private RedisIdGenerator redisIdGenerator;

    @Autowired
    private ZookeeperIdGenerator zookeeperIdGenerator;

    @RequestMapping(value = "/nextUniqueId", method = RequestMethod.GET)
    @ApiOperation(value = "获取全局唯一ID", notes = "获取分布式全局唯一ID", response = String.class, httpMethod = "GET")
    public String nextUniqueId(
            @RequestParam @ApiParam(value = "资源名字", required = true, defaultValue = "idgenerater") String name,
            @RequestParam @ApiParam(value = "资源Key", required = true, defaultValue = "X-Y") String key,
            @RequestParam @ApiParam(value = "递增值", required = true, defaultValue = "1") int step,
            @RequestParam @ApiParam(value = "长度", required = true, defaultValue = "8") int length) {
        return redisIdGenerator.nextUniqueId(name, key, step, length);
    }

    @RequestMapping(value = "/nextSequenceId", method = RequestMethod.GET)
    @ApiOperation(value = "获取全局唯一序号", notes = "获取分布式全局唯一序号", response = Integer.class, httpMethod = "GET")
    public int nextSequenceId(
            @RequestParam @ApiParam(value = "资源名字", required = true, defaultValue = "idgenerater") String name,
            @RequestParam @ApiParam(value = "资源Key", required = true, defaultValue = "X-Y") String key) {
        try {
            return zookeeperIdGenerator.nextSequenceId(name, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}