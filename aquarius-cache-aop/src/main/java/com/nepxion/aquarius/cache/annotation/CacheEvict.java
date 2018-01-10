package com.nepxion.aquarius.cache.annotation;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {
    /**
     * 缓存名字
     */
    String name() default "";

    /**
     * 缓存Key
     */
    String key() default "";

    /**
     * 是否全部清除缓存内容
     */
    boolean allEntries() default false;

    /**
     * 缓存清理是在方法调用前还是调用后
     */
    boolean beforeInvocation() default false;
}