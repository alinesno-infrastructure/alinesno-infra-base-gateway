package com.alinesno.infra.base.gateway.controller;

import com.alinesno.infra.base.gateway.entity.GroovyScriptEntity;
import com.alinesno.infra.base.gateway.service.IGroovyScriptService;
import com.alinesno.infra.common.core.rest.BaseController;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "GroovyScript")
@RestController
@RequestMapping("/api/infra/simple/crm/groovyScript")
public class GroovyScriptController extends BaseController<GroovyScriptEntity, IGroovyScriptService> {

    private static final Logger log = LoggerFactory.getLogger(GroovyScriptController.class);

    @Autowired
    private IGroovyScriptService service;

    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(Model model, DatatablesPageBean page) {
        log.debug("page = {}", page);
        return this.toDataInfo(model, this.getFeign(), page);
    }

    @Override
    public IGroovyScriptService getFeign() {
        return this.service;
    }
}
