package com.chensoul.oauth2.resource.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注是否为内部请求。如果是，这无需校验用户信息，意味着线程上下文中 cocktailUser 为空
 *
 * @author chensoul
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {

	/**
	 * 是否AOP统一处理
	 *
	 * @return false, true
	 */
	boolean value() default true;

}
