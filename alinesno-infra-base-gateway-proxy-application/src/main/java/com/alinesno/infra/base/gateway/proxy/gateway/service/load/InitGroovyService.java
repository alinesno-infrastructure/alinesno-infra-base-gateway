package com.alinesno.infra.base.gateway.proxy.gateway.service.load;

import com.alinesno.infra.base.gateway.core.util.Md5Utils;
import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.facade.entity.GroovyScript;
import com.alinesno.infra.base.gateway.domain.service.GroovyScriptService;
import com.alinesno.infra.base.gateway.proxy.gateway.service.DynamicGroovyService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description 初始化加载groovyScript规则引擎动态脚本
 * @author  JL
 * @date 2022/2/22
 * @version 1.0.0
 */
@Slf4j
@Service
public class InitGroovyService {

    @Resource
    private GroovyScriptService groovyScriptService;
    @Resource
    private DynamicGroovyService dynamicGroovyService;

    /**
     *  初始化加载groovyScript规则引擎动态脚本，并缓存实例化对象
     */
    @PostConstruct
    public void initLoadGroovyScript(){
        //查询已启用的groovyScript规则引擎动态脚本
        GroovyScript groovyScript = new GroovyScript();
        groovyScript.setStatus(Constants.YES);
        List<GroovyScript> groovyScriptList = groovyScriptService.list(groovyScript);
        if (CollectionUtils.isEmpty(groovyScriptList)){
            log.info("未初始化groovyScript规则引擎动态脚本，脚本集合数量：0！");
            return ;
        }
        String md5;
        for (GroovyScript script : groovyScriptList){
            md5 = Md5Utils.md5Str(script.getId() + script.getContent());
            dynamicGroovyService.instance(script, md5, true);
        }
    }

}
