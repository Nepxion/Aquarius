package com.nepxion.aquarius.idgenerator.local.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.util.DateUtil;
import com.nepxion.aquarius.idgenerator.local.LocalIdGenerator;

@Component("localIdGeneratorImpl")
public class LocalIdGeneratorImpl implements LocalIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(LocalIdGeneratorImpl.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    private static final long DEFAULT_START_TIMESTAMP = 1483200000000L; // 2017-01-01 00:00:00:000

    private volatile Map<String, SnowflakeIdGenerator> idGeneratorMap = new ConcurrentHashMap<String, SnowflakeIdGenerator>();

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    @Override
    public long nextUniqueId(long dataCenterId, long workerId) {
        return nextUniqueId(DEFAULT_START_TIMESTAMP, dataCenterId, workerId);
    }

    @Override
    public long nextUniqueId(String startTimestamp, long dataCenterId, long workerId) throws Exception {
        return nextUniqueId(DateUtil.parseDate(startTimestamp, DATE_FORMAT).getTime(), dataCenterId, workerId);
    }

    @Override
    public long nextUniqueId(long startTimestamp, long dataCenterId, long workerId) {
        long nextUniqueId = getIdGenerator(startTimestamp, dataCenterId, workerId).nextId();

        if (frequentLogPrint) {
            LOG.info("Next unique id is {} for startTimestamp={}, dataCenterId={}, workerId={}", nextUniqueId, startTimestamp, dataCenterId, workerId);
        }

        return nextUniqueId;
    }

    private SnowflakeIdGenerator getIdGenerator(long startTimestamp, long dataCenterId, long workerId) {
        String key = dataCenterId + "-" + workerId;

        SnowflakeIdGenerator idGenerator = idGeneratorMap.get(key);
        if (idGenerator == null) {
            SnowflakeIdGenerator newIdGnerator = new SnowflakeIdGenerator(startTimestamp, dataCenterId, workerId);
            idGenerator = idGeneratorMap.putIfAbsent(key, newIdGnerator);
            if (idGenerator == null) {
                idGenerator = newIdGnerator;
            }
        }

        return idGenerator;
    }
}