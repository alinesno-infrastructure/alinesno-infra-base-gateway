package com.alinesno.infra.base.gateway.manage.aop;

import com.alinesno.infra.base.gateway.manage.ManagerAccountEntity;

import java.util.Map;

/**
 * 自定义数据权限拦截
 * 
 * @author  LuoAnDong
 * @since 2020年3月22日 下午5:56:47
 */
public interface DateFilterParent {

	/**
	 * 前端参数
	 * 
	 * @param paramMap 参数
	 * @param account  当前登录用户
	 */
	void filterMap(Map<String, Object> paramMap, ManagerAccountEntity account);

}
