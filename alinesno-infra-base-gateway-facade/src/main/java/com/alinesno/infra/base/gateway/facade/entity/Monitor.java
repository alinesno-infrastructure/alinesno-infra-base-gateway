package com.alinesno.infra.base.gateway.facade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description 告警监控实体类
 * @author JL
 * @date 2021/04/14
 * @version v1.0.0
 */
@Entity
@Table(name="monitor")
@Data
public class Monitor extends BaseEntity {

    /**
     * 主键，同routeId
     */
    @Id
    @NotNull(message = "告警ID不能为空")
    @Size(min = 2, max = 40, message = "告警ID长度必需在2到40个字符内")
    private String id;

    /**
     * 告警状态：0启用，1禁用，2告警
     */
    @NotNull(message = "告警状态不能为空")
    @Column(name = "status" )
    private String status;

    /**
     * 通知接收邮箱 emails
     */
    @NotNull(message = "通知接收邮箱不能为空")
    @Column(name = "emails" )
    private String emails;

    /**
     * 告警邮件主题附带内容
     */
    @Column(name = "topic" )
    private String topic;

    /**
     * 告警频率：30m,1h,5h,12h,24h
     */
    @NotNull(message = "告警频率不能为空")
    @Column(name = "frequency" )
    @Size(min = 2, max = 4, message = "告警频率不能为空")
    private String frequency;

    /**
     * 告警重试，0开启，1禁用
     */
    @NotNull(message = "告警重试不能为空")
    @Column(name = "recover" )
    private String recover;

    /**
     * 告警时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "alarmTime" )
    private Date alarmTime;

    /**
     * 发送告警时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "sendTime" )
    private Date sendTime;

    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "更新时间不能为空")
    @Column(name = "updateTime" )
    private Date updateTime;

}
