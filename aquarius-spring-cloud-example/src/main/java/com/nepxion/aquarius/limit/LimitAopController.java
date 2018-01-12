package com.nepxion.aquarius.limit;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import io.swagger.annotations.Api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nepxion.aquarius.limit.annotation.Limit;

@RestController
@Api(tags = { "分布式限流注解接口" })
public class LimitAopController {
    private static final Logger LOG = LoggerFactory.getLogger(LimitAopController.class);

    @RequestMapping(value = "/doG", method = RequestMethod.GET)
    @Limit(name = "limit", key = "#id1 + \"-\" + #id2", limitPeriod = 10, limitCount = 5)
    public String doG(String id1, String id2) {
        LOG.info("doG");

        return "G";
    }
}