package com.alinesno.infra.base.gateway.manage.aop;

/**
 * 数据权限过滤角色
 * 
 * @author  WeiXiaoJin
 * @since 2019年9月15日 下午5:02:33
 */
public enum DataFilterRole {

	APPLICATION("application_id"), // 所属应用 应用权限: 只能看到所属应用的权限【默认】
	TENANT("tenant_id"), // 所属租户 , 租户权限
	OPERATOR("operator_id"), // 操作员 用户权限: 只能看到自己操作的数据
	FIELD("field_id"), // 字段权限：部分字段权限无法加密或者不显示，或者大于某个值
	DEPARTMENT("department"), // 部门权限: 只能看到自己所在部门的数据
	CUSTOM("custom"), // 自定义权限
	NOT_FILTER("not_filter"); // 不做数据权限拦截

	private String roleFiled;

	DataFilterRole(String roleFiled) {
		this.roleFiled = roleFiled;
	}

	public String getRoleFiled() {
		return roleFiled;
	}

}
