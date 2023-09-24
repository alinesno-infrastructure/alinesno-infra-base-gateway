package com.alinesno.infra.base.gateway.formwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description
 * @author JL
 * @date 2022/2/21
 * @version v1.0.0
 */
@Entity
@Table(name="groovyscript")
@Data
public class GroovyScript {

    /**
     * 主键，同route_id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 网关ID
     */
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    @Column(name = "routeId" )
    private String routeId;

    /**
     * 脚本名称
     */
    /**
     * 网关ID
     */
    @NotNull(message = "网关路由规则引擎动态脚本组件名称不能为空")
    @Size(min = 2, max = 40, message = "网关路由规则引擎动态脚本名称长度必需在2到40个字符内")
    @Column(name = "name" )
    private String name;

    /**
     * 脚本内容
     */
    @NotNull(message = "网关路由规则引擎动态脚本组件代码不能为空")
    @Column(name = "content" )
    private String content;

    /**
     * 扩展内容,参数json
     */
    @Column(name = "extendInfo" )
    private String extendInfo;

    /**
     * 执行事件,request|response
     */
    @NotNull(message = "网关路由执行事件不能为空")
    @Column(name = "event" )
    private String event;

    /**
     * 顺序
     */
    @Column(name = "orderNum" )
    private Integer orderNum;
    /**
     * 状态
     */
    @Column(name = "status" )
    private String status;
    /**
     * 备注
     */
    @Column(name = "remarks" )
    private String remarks;
    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime" )
    private Date createTime;
    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updateTime" )
    private Date updateTime;
}
