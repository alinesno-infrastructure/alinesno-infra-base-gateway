package com.alinesno.infra.base.gateway.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Entity
@Table(name="statistical_data")
@Data
public class StatisticalData {

    @Id
    @NotNull(message = "id不能为空")
    String id;

    @Column(name = "accountId")
    String accountId;

    @Column(name = "countTimeKey")
    String countTimeKey;

    @Column(name = "totalCount")
    Long totalCount;

    @Column(name = "successCount")
    Long successCount;

    @Column(name = "failCount")
    Long failCount;
}
