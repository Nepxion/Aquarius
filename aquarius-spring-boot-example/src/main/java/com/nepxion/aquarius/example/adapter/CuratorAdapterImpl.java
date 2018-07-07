package com.nepxion.aquarius.example.adapter;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.io.IOException;

import com.nepxion.aquarius.common.curator.adapter.CuratorAdapter;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.curator.handler.CuratorHandlerImpl;
import com.nepxion.aquarius.common.curator.util.CuratorUtil;
import com.nepxion.aquarius.common.property.AquariusProperties;

public class CuratorAdapterImpl implements CuratorAdapter {
    @Override
    public CuratorHandler getCuratorHandler(String prefix) {
        // 来自远程配置中心的内容
        String propertyConfigContent = "...";

        AquariusProperties properties = null;
        try {
            properties = CuratorUtil.createPropertyConfig(propertyConfigContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new CuratorHandlerImpl(properties, prefix);
    }
}