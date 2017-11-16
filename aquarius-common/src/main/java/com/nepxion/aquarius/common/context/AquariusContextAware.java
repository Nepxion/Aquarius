package com.nepxion.aquarius.common.context;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AquariusContextAware implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    static {
        System.out.println("");
        System.out.println("╔═══╗");
        System.out.println("║╔═╗║");
        System.out.println("║║ ║╠══╦╗╔╦══╦═╦╦╗╔╦══╗");
        System.out.println("║╚═╝║╔╗║║║║╔╗║╔╬╣║║║══╣");
        System.out.println("║╔═╗║╚╝║╚╝║╔╗║║║║╚╝╠══║");
        System.out.println("╚╝─╚╩═╗╠══╩╝╚╩╝╚╩══╩══╝");
        System.out.println("      ║║");
        System.out.println("      ╚╝");
        System.out.println("Nepxion Aquarius  v1.0.0.RELEASE");
        System.out.println("");
    }

    private AquariusContextAware() {
    }

    private static void setContext(ApplicationContext applicationContext) {
        AquariusContextAware.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        AquariusContextAware.setContext(applicationContext);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static String getProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }

    public static boolean isProfileActive(String profile) {
        return applicationContext.getEnvironment().acceptsProfiles(profile);
    }
}