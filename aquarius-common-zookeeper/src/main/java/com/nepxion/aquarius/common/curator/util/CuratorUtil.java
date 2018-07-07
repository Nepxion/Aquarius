package com.nepxion.aquarius.common.curator.util;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.property.AquariusProperties;

public class CuratorUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CuratorUtil.class);

    // 创建Property格式的配置文件
    public static AquariusProperties createPropertyFileConfig(String propertyConfigPath) throws IOException {
        LOG.info("Start to read {}...", propertyConfigPath);

        return new AquariusProperties(propertyConfigPath, AquariusConstant.ENCODING_GBK, AquariusConstant.ENCODING_UTF_8);
    }

    // 创建Property格式的配置文件
    public static AquariusProperties createPropertyConfig(String propertyConfigContent) throws IOException {
        return new AquariusProperties(propertyConfigContent, AquariusConstant.ENCODING_UTF_8);
    }
}