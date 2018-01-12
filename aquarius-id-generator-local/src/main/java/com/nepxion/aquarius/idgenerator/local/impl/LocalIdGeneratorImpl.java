package com.nepxion.aquarius.idgenerator.local.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
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
import com.nepxion.aquarius.common.util.StringUtil;
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
    public String nextUniqueId(long dataCenterId, long machineId) {
        return nextUniqueId(DEFAULT_START_TIMESTAMP, dataCenterId, machineId);
    }

    @Override
    public String nextUniqueId(String startTimestamp, long dataCenterId, long machineId) throws Exception {
        return nextUniqueId(DateUtil.parseDate(startTimestamp, DATE_FORMAT).getTime(), dataCenterId, machineId);
    }

    @Override
    public String nextUniqueId(long startTimestamp, long dataCenterId, long machineId) {
        String nextUniqueId = getIdGenerator(startTimestamp, dataCenterId, machineId).nextId();

        if (frequentLogPrint) {
            LOG.info("Next unique id is {} for startTimestamp={}, dataCenterId={}, machineId={}", nextUniqueId, startTimestamp, dataCenterId, machineId);
        }

        return nextUniqueId;
    }

    @Override
    public String[] nextUniqueIds(long dataCenterId, long machineId, int count) {
        return nextUniqueIds(DEFAULT_START_TIMESTAMP, dataCenterId, machineId, count);
    }

    @Override
    public String[] nextUniqueIds(String startTimestamp, long dataCenterId, long machineId, int count) throws Exception {
        return nextUniqueIds(DateUtil.parseDate(startTimestamp, DATE_FORMAT).getTime(), dataCenterId, machineId, count);
    }

    @Override
    public String[] nextUniqueIds(long startTimestamp, long dataCenterId, long machineId, int count) {
        String[] nextUniqueIds = getIdGenerator(startTimestamp, dataCenterId, machineId).nextIds(count);

        if (frequentLogPrint) {
            LOG.info("Next unique ids is {} for startTimestamp={}, dataCenterId={}, machineId={}, count={}", StringUtil.convert(nextUniqueIds), startTimestamp, dataCenterId, machineId, count);
        }

        return nextUniqueIds;
    }

    private SnowflakeIdGenerator getIdGenerator(long startTimestamp, long dataCenterId, long machineId) {
        String key = dataCenterId + "-" + machineId;

        SnowflakeIdGenerator idGenerator = idGeneratorMap.get(key);
        if (idGenerator == null) {
            SnowflakeIdGenerator newIdGnerator = new SnowflakeIdGenerator(startTimestamp, dataCenterId, machineId);
            idGenerator = idGeneratorMap.putIfAbsent(key, newIdGnerator);
            if (idGenerator == null) {
                idGenerator = newIdGnerator;
            }
        }

        return idGenerator;
    }
}