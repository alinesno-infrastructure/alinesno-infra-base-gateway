package com.alinesno.infra.base.gateway.core.base;

import com.alinesno.infra.base.gateway.core.entity.BaseEntity;
import com.alinesno.infra.base.gateway.core.util.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.Set;

/**
 * @description 父控制器类，提供基础验证方法
 * @author jianglong
 * @date 2020/05/16
 * @version v1.0.0
 */
public class BaseRest<E extends BaseEntity> {

    @Autowired
    private Validator validator;

    /**
     * 实体类参数有效性验证
     * @param t
     */
    public <T> void validate(T t) {
        validate(t, false, new Class[0]);
    }

    /**
     * 实体类参数有效性验证
     * @param t 验证的实体对象
     * @param isMulti 验证的实体对象
     * @param groups 验证组
     * 验证失败：将错误信息添加到message中
     */
    public <T> void validate(T t, boolean isMulti, Class<?> ...groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(t, groups);
        if (!constraintViolationSet.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator<ConstraintViolation<T>> iterator = constraintViolationSet.iterator();
            //true表示多个验证一起组合后抛出
            if (isMulti){
                while (iterator.hasNext()) {
                    ConstraintViolation<T> violation =iterator.next();
                    sb.append(",").append(violation.getMessage());
                }
            }else {
                ConstraintViolation<T> violation =iterator.next();
                sb.append(",").append(violation.getMessage());
            }
            throw new ValidationException(sb.substring(1));
        }
    }

    /***
     * 获取分页索引
     * @param currentPage
     * @return
     */
    public int getCurrentPage(Integer currentPage){
        return currentPage == null? Constants.CURRENT_PAGE: currentPage;
    }

    /**
     * 获取分页展示行数
     * @param pageSize
     * @return
     */
    public int getPageSize(Integer pageSize){
        return pageSize == null? Constants.PAGE_SIZE: pageSize;
    }


    /**
     * 设置操作用户
     * @param entity
     * @param operatorId
     * @return
     */
    @Deprecated
    protected E toExample(E entity, Long operatorId){
        if(operatorId == null){
            operatorId = 0L ;
        }
        entity.setOperatorId(operatorId);
        return entity;
    }

    /**
     * 设置操作用户
     * @param entity
     * @param orgId
     * @return
     */
    protected E toExampleOrg(E entity, Long orgId){
        if(orgId == null){
            orgId = 0L ;
        }
        entity.setOrgId(orgId);
        return entity;
    }
}
