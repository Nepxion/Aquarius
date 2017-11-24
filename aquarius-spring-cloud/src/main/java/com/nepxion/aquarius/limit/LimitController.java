package com.nepxion.aquarius.limit;

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

@RestController
public class LimitController {
    @Autowired
    private LimitExecutor limitExecutor;

    @RequestMapping(value = "/tryAccess", method = RequestMethod.GET)
    public boolean tryAccess(@RequestParam String name, @RequestParam String key, @RequestParam int limitPeriod, @RequestParam int limitCount) {
        return limitExecutor.tryAccess(name, key, limitPeriod, limitCount);
    }
}