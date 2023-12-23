package com.alinesno.infra.base.gateway.core.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Size;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@MappedSuperclass
public class BaseEntity {

    @Size(min = 2, max = 64, message = "2到64个字符内")
    @Column(name = "operatorId" )
    private String operatorId;

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
}
