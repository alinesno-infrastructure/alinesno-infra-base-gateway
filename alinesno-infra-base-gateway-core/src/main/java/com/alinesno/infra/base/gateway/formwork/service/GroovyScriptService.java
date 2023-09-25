package com.alinesno.infra.base.gateway.formwork.service;

import com.alinesno.infra.base.gateway.formwork.base.BaseService;
import com.alinesno.infra.base.gateway.formwork.dao.GroovyScriptDao;
import com.alinesno.infra.base.gateway.formwork.entity.GroovyScript;
import com.alinesno.infra.base.gateway.formwork.util.GroovyScriptUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @description 规则引擎GroovyScript动态脚本业务处理类
 * @author JL
 * @date 2022/2/21
 * @version v1.0.0
 */
@Slf4j
@Service
public class GroovyScriptService extends BaseService<GroovyScript, Long, GroovyScriptDao> {

    @Resource
    private GroovyScriptDao groovyScriptDao;

    private final static String ORDER_NUM = "orderNum";

    /**
     * 获取routeId下最大orderNum
     * @param routeId
     * @param event
     * @return
     */
    public int findMaxOrderNum(String routeId, String event) {
        Integer orderNum = groovyScriptDao.findMaxOrderNum(routeId, event);
        return orderNum == null ? 1 : orderNum;
    }

    /**
     * 获取routeId下status为0（启用）的id集合，并按id顺序排序
     * @param routeId
     * @return
     */
    public List<Long> findIdList(String routeId){
        return groovyScriptDao.findIdList(routeId);
    }

    /**
     * 删除动态脚本
     * @param groovyScript
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void delete(GroovyScript groovyScript){
        super.delete(groovyScript);
        //重新排序
        GroovyScript qGroovyScript = new GroovyScript();
        qGroovyScript.setRouteId(groovyScript.getRouteId());
        List<GroovyScript> groovyScriptList = list(qGroovyScript);
        if (CollectionUtils.isEmpty(groovyScriptList)){
            return;
        }
        int i = 1;
        for (GroovyScript dbGroovyScript : groovyScriptList){
            dbGroovyScript.setOrderNum(i++);
            update(dbGroovyScript);
        }
    }

    @Override
    public List<GroovyScript> list(GroovyScript script){
        return list(script, ORDER_NUM);
    }

    /**
     * 向上移动
     * @param groovyScript
     */
    public boolean upOrderNum(GroovyScript groovyScript) {
        if (groovyScript.getOrderNum() <= 1) {
            return false;
        }
        return remvoeOrderNum(groovyScript, true);
    }

    /**
     * 向下移动
     * @param groovyScript
     */
    public boolean downOrderNum(GroovyScript groovyScript) {
        int maxOrderNum = findMaxOrderNum(groovyScript.getRouteId(), groovyScript.getEvent());
        if (groovyScript.getOrderNum() >= maxOrderNum) {
            return false;
        }
        return remvoeOrderNum(groovyScript, false);
    }

    /**
     * 移动序号
     * @param groovyScript
     * @param isUp
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean remvoeOrderNum(GroovyScript groovyScript, boolean isUp) {
        String routeId = groovyScript.getRouteId();
        long id = groovyScript.getId();
        int orderNum = groovyScript.getOrderNum();
        GroovyScript qGroovyScript = new GroovyScript();
        qGroovyScript.setRouteId(routeId);
        List<GroovyScript> groovyScriptList = list(qGroovyScript, ORDER_NUM);
        if (CollectionUtils.isEmpty(groovyScriptList)) {
            return false;
        }

        Optional<GroovyScript> optional ;
        if (isUp){
            optional = groovyScriptList.stream()
                    .filter(g->g.getId() != id && g.getEvent().equals(groovyScript.getEvent()) && g.getOrderNum() <= orderNum)
                    .sorted((g1,g2)->g2.getOrderNum() - g1.getOrderNum())
                    .findFirst();
        }else {
            optional = groovyScriptList.stream()
                    .filter(g->g.getId() != id && g.getEvent().equals(groovyScript.getEvent()) && g.getOrderNum() >= orderNum)
                    .findFirst();
        }

        if (!optional.isPresent()){
            return false;
        }

        //交换序号
        GroovyScript dbGroovyScript = optional.get();
        int dbOrderNum = dbGroovyScript.getOrderNum();
        groovyScript.setOrderNum(dbOrderNum);
        update(groovyScript);

        dbOrderNum = isUp ? dbOrderNum + 1 : dbOrderNum - 1;
        dbGroovyScript.setOrderNum(dbOrderNum);
        update(dbGroovyScript);

        return true;
    }

    /**
     * 初始化GroovyScript规则擎动态脚本，并实例化对象
     * @param script
     */
    public Object instance(GroovyScript script) throws Exception {
        try {
            return GroovyScriptUtils.newObjectInstance(script.getContent());
        }catch(CompilationFailedException e){
            log.error("groovyScript规则引擎动态脚本编译错误！脚本ID：" + script.getId() + "，文件名" + script.getName() , e);
            throw e;
        }catch (InstantiationException | IllegalArgumentException | SecurityException | IllegalAccessException e){
            log.error("groovyScript规则引擎动态脚本实例化错误！脚本ID：" + script.getId() + "，文件名" + script.getName() , e);
            throw e;
        }catch(Exception e){
            log.error("groovyScript规则引擎动态脚本加载错误！脚本ID：" + script.getId() + "，文件名" + script.getName() , e);
            throw e;
        }
    }

}
