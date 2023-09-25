package com.alinesno.infra.base.gateway.formwork.dao;

import com.alinesno.infra.base.gateway.formwork.entity.ApiDoc;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 网关路由API接口文档数据层操作接口
 * @author JL
 * @date 2020/11/25
 * @version v1.0.0
 */
public interface ApiDocDao extends JpaRepository<ApiDoc, String> {

}
