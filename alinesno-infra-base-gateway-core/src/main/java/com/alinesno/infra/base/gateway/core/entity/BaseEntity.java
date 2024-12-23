package com.alinesno.infra.base.gateway.core.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Data
@MappedSuperclass
public class BaseEntity {

    @Min(value = 2 , message = "2个字符以上")
    @Column(name = "operatorId" )
    private Long operatorId;

    @Min(value = 2 , message = "2个字符以上")
    @Column(name = "orgId" , nullable = true)
    private Long orgId ; // 组织id

}
