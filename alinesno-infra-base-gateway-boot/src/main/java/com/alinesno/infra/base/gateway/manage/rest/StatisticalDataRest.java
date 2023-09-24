package com.alinesno.infra.base.gateway.manage.rest;

import com.alinesno.infra.base.gateway.formwork.base.BaseRest;
import com.alinesno.infra.base.gateway.formwork.service.StatisticalDataService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luoxiaodong
 * @data 2023/05/08 14:11
 */
@Slf4j
@RestController
@RequestMapping("/statistical")
public class StatisticalDataRest extends BaseRest {
    @Resource
    private StatisticalDataService statisticalDataService;



}
