package com.alinesno.infra.base.gateway.formwork.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @author luoxiaodong
 * @data 2023/04/25 17:22
 */
@Entity
@Table(name="account")
@Data
public class Account {
    @Id
    @NotNull(message = "ID不能为空")
    @Size(min = 2, max = 64, message = "ID长度必需在2到64个字符内")
    private String id;

    @NotNull(message = "账号id不能为空")
    @Size(min = 4, max = 32, message = "账号id长度必需在2到32个字符内")
    private String accountId;

    @NotNull(message = "用户标识不能为空")
    @Size(min = 2, max = 256, message = "用户标识长度必需在2到256个字符内")
    private String authToken;

}
