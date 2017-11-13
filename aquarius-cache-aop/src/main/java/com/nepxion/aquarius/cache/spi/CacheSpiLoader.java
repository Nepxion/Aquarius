package com.nepxion.aquarius.cache.spi;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.cache.constant.CacheConstant;
import com.nepxion.aquarius.common.spi.AquariusSpiLoader;

public final class CacheSpiLoader {
    private static final CacheSpi CACHE_SPI = AquariusSpiLoader.loadFromProperties(CacheSpi.class, CacheConstant.SPI_NAME);
    // private static final CacheSpi CACHE_SPI = AquariusSpiLoader.load(CacheSpi.class);

    public static CacheSpi load() {
        return CACHE_SPI;
    }
}