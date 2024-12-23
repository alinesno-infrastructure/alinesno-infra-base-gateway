package com.alinesno.infra.base.gateway.rest.aop;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {

	/**
	 * 自定义权限Bean过滤名称
	 * 
	 * @return
	 */
	public String roleBean() default "";

	/**
	 * 默认为组织权限
	 * 
	 * @return
	 */
	public DataFilterRole type() default DataFilterRole.ORGANIZATION;

}