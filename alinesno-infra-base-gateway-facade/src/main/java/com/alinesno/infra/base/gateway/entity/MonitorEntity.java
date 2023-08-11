package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

/**
 * 告警监控实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("monitor")
public class MonitorEntity extends InfraBaseEntity {

    /**
     * 通知接收邮箱
     */
    @TableField("emails")
    private String emails;

    /**
     * 告警邮件主题附带内容
     */
    @TableField("topic")
    private String topic;

    /**
     * 告警频率：30m,1h,5h,12h,24h
     */
    @TableField("frequency")
    private String frequency;

    /**
     * 告警重试，0开启，1禁用
     */
    @TableField("recover")
    private String recover;

    /**
     * 告警时间
     */
    @TableField("alarm_time")
    private Date alarmTime;

    /**
     * 发送告警时间
     */
    @TableField("send_time")
    private Date sendTime;

    // 省略getter和setter方法

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getRecover() {
        return recover;
    }

    public void setRecover(String recover) {
        this.recover = recover;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
