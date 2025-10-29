package com.alinesno.infra.base.gateway.facade.bean;

import lombok.Data;

import java.util.List;

/**
 * @description
 * @author JL
 * @date 2020/07/09
 * @version v1.0.0
 */
@Data
public class CountReq extends BaseReq implements java.io.Serializable {
    /**
     * 负载ID
     */
    private String balancedId;
    /**
     * 路由集合
     */
    private List<String> routeIds;
    /**
     * 统计类型
     */
    private String dateType;
    /**
     * 名称
     */
    private String name;
    /**
     * 日期
     */
    private String date;
    /**
     * 分组
     */
    private String groupCode;

    private Integer currentPage;
    private Integer pageSize;
}
