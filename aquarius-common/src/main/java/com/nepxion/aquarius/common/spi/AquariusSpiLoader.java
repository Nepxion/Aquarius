package com.nepxion.aquarius.common.spi;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.property.AquariusProperties;
import com.nepxion.aquarius.common.property.AquariusPropertiesManager;

public final class AquariusSpiLoader {
    private static final Logger LOG = LoggerFactory.getLogger(AquariusSpiLoader.class);

    public static <S> List<S> loadAll(Class<S> serviceClass) {
        List<S> services = new ArrayList<S>();

        Iterator<S> iterator = ServiceLoader.load(serviceClass).iterator();
        while (iterator.hasNext()) {
            S service = iterator.next();

            LOG.info("SPI loaded - interface={}, implementation={}", serviceClass.getName(), service.getClass().getName());

            services.add(service);
        }

        if (CollectionUtils.isEmpty(services)) {
            String error = "It can't be retrieved any SPI implementations=" + serviceClass.getName();
            LOG.error(error);
            throw new IllegalArgumentException(error);
        }

        return services;
    }

    public static <S> S load(Class<S> serviceClass) {
        Iterator<S> iterator = ServiceLoader.load(serviceClass).iterator();
        if (iterator.hasNext()) {
            S service = iterator.next();

            LOG.info("SPI loaded - interface={}, implementation={}", serviceClass.getName(), service.getClass().getName());

            return service;
        }

        String error = "It can't be retrieved SPI implementation=" + serviceClass.getName();
        LOG.error(error);
        throw new IllegalArgumentException(error);
    }

    public static <S> S load(Class<S> serviceClass, String serviceImplClassName) {
        Iterator<S> iterator = ServiceLoader.load(serviceClass).iterator();
        while (iterator.hasNext()) {
            S service = iterator.next();
            if (StringUtils.equalsIgnoreCase(service.getClass().getName(), serviceImplClassName.trim())) {
                LOG.info("SPI loaded - interface={}, implementation={}", serviceClass.getName(), service.getClass().getName());

                return service;
            }
        }

        String error = "It can't be retrieved SPI implementation=" + serviceImplClassName.trim() + " with interface=" + serviceClass.getName();
        LOG.error(error);
        throw new IllegalArgumentException(error);
    }

    public static <S> S loadFromProperties(Class<S> serviceClass, String serviceKey) {
        AquariusProperties properties = AquariusPropertiesManager.getProperties();
        String serviceImplClassName = properties.getString(serviceKey);

        return load(serviceClass, serviceImplClassName);
    }
}