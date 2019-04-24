package com.nepxion.aquarius.common.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.banner.BannerConstant;
import com.nepxion.banner.Description;
import com.nepxion.banner.LogoBanner;
import com.nepxion.banner.NepxionBanner;
import com.taobao.text.Color;

@Configuration
@ComponentScan(basePackages = { "com.nepxion.aquarius.common.context" })
public class AquariusConfiguration {
    static {
        /*String bannerShown = System.getProperty(BannerConstant.BANNER_SHOWN, "true");
        if (Boolean.valueOf(bannerShown)) {
            System.out.println("");
            System.out.println("╔═══╗");
            System.out.println("║╔═╗║");
            System.out.println("║║ ║╠══╦╗╔╦══╦═╦╦╗╔╦══╗");
            System.out.println("║╚═╝║╔╗║║║║╔╗║╔╬╣║║║══╣");
            System.out.println("║╔═╗║╚╝║╚╝║╔╗║║║║╚╝╠══║");
            System.out.println("╚╝ ╚╩═╗╠══╩╝╚╩╝╚╩══╩══╝");
            System.out.println("      ║║");
            System.out.println("      ╚╝");
            System.out.println("Nepxion Aquarius  v" + AquariusConstant.AQUARIUS_VERSION);
            System.out.println("");
        }*/

        LogoBanner logoBanner = new LogoBanner(AquariusConfiguration.class, "/com/nepxion/aquarius/resource/logo.txt", "Welcome to Nepxion", 8, 5, new Color[] { Color.red, Color.green, Color.cyan, Color.blue, Color.yellow, Color.magenta, Color.red, Color.green }, true);

        NepxionBanner.show(logoBanner, new Description(BannerConstant.VERSION + ":", AquariusConstant.AQUARIUS_VERSION, 0, 1), new Description(BannerConstant.GITHUB + ":", BannerConstant.NEPXION_GITHUB + "/Aquarius", 0, 1));
    }
}