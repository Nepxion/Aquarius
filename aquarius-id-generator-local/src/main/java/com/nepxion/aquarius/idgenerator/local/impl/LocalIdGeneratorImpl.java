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
import com.nepxion.aquarius.idgenerator.local.LocalIdGenerator;

@Component("localIdGeneratorImpl")
public class LocalIdGeneratorImpl implements LocalIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(LocalIdGeneratorImpl.class);
    private volatile Map<String, SnowflakeIdGenerator> generatorMap = new ConcurrentHashMap<String, SnowflakeIdGenerator>();

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    @Override
    public long nextUniqueId(long workerId, long dataCenterId) {
        long nextUniqueId = getIdGenerator(workerId, dataCenterId).nextId();

        if (frequentLogPrint) {
            LOG.info("Next unique id is {} for workerId={}, dataCenterId={}", nextUniqueId, workerId, dataCenterId);
        }

        return nextUniqueId;
    }

    private SnowflakeIdGenerator getIdGenerator(long workerId, long dataCenterId) {
        String key = workerId + "-" + dataCenterId;

        SnowflakeIdGenerator idGenerator = generatorMap.get(key);
        if (idGenerator == null) {
            SnowflakeIdGenerator newIdGnerator = new SnowflakeIdGenerator(workerId, dataCenterId);
            idGenerator = generatorMap.putIfAbsent(key, newIdGnerator);
            if (idGenerator == null) {
                idGenerator = newIdGnerator;
            }
        }

        return idGenerator;
    }
}