package com.nepxion.aquarius.limit;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
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

@RestController
@Api(tags = { "分布式限流接口" })
public class LimitController {
    @Autowired
    private LimitExecutor limitExecutor;

    @RequestMapping(value = "/tryAccess", method = RequestMethod.GET)
    @ApiOperation(value = "请求分布式限流许可", notes = "在给定的时间段里最多的访问限制次数(超出次数返回false)；等下个时间段开始，才允许再次被访问(返回true)，周而复始", response = Boolean.class, httpMethod = "GET")
    public boolean tryAccess(
            @RequestParam @ApiParam(value = "资源名字", required = true, defaultValue = "limit") String name,
            @RequestParam @ApiParam(value = "资源Key", required = true, defaultValue = "X-Y") String key,
            @RequestParam @ApiParam(value = "给定的时间段(单位秒)", required = true, defaultValue = "10") int limitPeriod,
            @RequestParam @ApiParam(value = "最多的访问限制次数", required = true, defaultValue = "5") int limitCount) {
        return limitExecutor.tryAccess(name, key, limitPeriod, limitCount);
    }
}