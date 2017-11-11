package com.nepxion.aquarius.lock.spi;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import com.nepxion.aquarius.common.spi.AquariusSpiLoader;

public final class LockSpiLoader {
    private static final LockSpi LOCK_SPI = AquariusSpiLoader.load(LockSpi.class);

    public static LockSpi load() {
        return LOCK_SPI;
    }
}