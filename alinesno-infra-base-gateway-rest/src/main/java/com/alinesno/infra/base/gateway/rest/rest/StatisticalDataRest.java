package com.alinesno.infra.base.gateway.rest.rest;

import com.alinesno.infra.base.gateway.core.base.BaseRest;
import com.alinesno.infra.base.gateway.core.service.StatisticalDataService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author  luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/statistical")
public class StatisticalDataRest extends BaseRest {
    @Resource
    private StatisticalDataService statisticalDataService;



}
