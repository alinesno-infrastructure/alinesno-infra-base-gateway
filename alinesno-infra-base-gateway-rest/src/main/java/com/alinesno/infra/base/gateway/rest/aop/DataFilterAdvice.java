package com.alinesno.infra.base.gateway.rest.aop;

import com.alinesno.infra.base.gateway.facade.bean.BaseReq;
import com.alinesno.infra.base.gateway.facade.entity.BaseEntity;
import com.alinesno.infra.common.web.adapter.base.dto.ManagerAccountDto;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * 数据权限过滤
 *
 * @author WeiXiaoJin
 * @since 2019年9月15日 下午5:00:19
 */
@Slf4j
@Component
@Aspect
public class DataFilterAdvice {

    @Autowired
    private HttpServletRequest request;

    // 拦截指定的方法,这里指只拦截TestService.getResultData这个方法
    @Pointcut("@annotation(com.alinesno.infra.base.gateway.rest.aop.DataFilter)")
    public void pointcut() {

    }

    // 执行方法前的拦截方法
    @SuppressWarnings("rawtypes")
    @Before("pointcut()")
    public void doBeforeMethod(JoinPoint joinPoint) throws NoSuchMethodException, SecurityException {
        // 获取目标方法的参数信息
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs(); // 请求接收的参数args
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Class[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method methodClass = targetClass.getMethod(methodName, parameterTypes);

        DataFilter dataFilter = methodClass.getAnnotation(DataFilter.class); // .getAnnotations();
        String beanName = dataFilter.roleBean();

        DataFilterRole type = dataFilter.type();
        log.debug("data filter type :{} , request:{}", type, request);

        for (Object argItem : args) {
            log.debug("argItem:{}", ToStringBuilder.reflectionToString(argItem));

            filterDataParams(argItem, type, beanName);
        }

    }

    /**
     * 添加过滤参数
     *
     * @param reqObject
     * @param type
     */

    private void filterDataParams(Object reqObject, DataFilterRole type, String beanName) {
        try {
            // 获取当前用户
            ManagerAccountDto account = CurrentAccountJwt.get();
            if (account == null) {
                log.warn("无法获取当前用户信息");
                return;
            }

            // 公共方法设置操作员ID和组织ID
            setCommonIds(reqObject, account);

            switch (type) {
                case OPERATOR:
                    setOperatorId(reqObject, account);
                    break;
                case DEPARTMENT:
                    // 处理部门权限
                    log.info("处理部门权限逻辑");
                    break;
                case ORGANIZATION:
                    setOrgId(reqObject, account);
                    break;
                case APPLICATION:
                    // 处理应用权限
                    log.info("处理应用权限逻辑");
                    break;
                case CUSTOM:
                    // 处理自定义权限
                    log.info("处理自定义权限逻辑");
                    break;
                default:
                    log.warn("未知的数据过滤角色类型: " + type);
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 为请求对象设置通用ID属性
     * 此方法主要用于设置除特殊属性外的通用属性，如操作者ID等
     * @param reqObject 请求对象，可以是BaseReq或BaseEntity的实例
     * @param account 管理员账户信息，用于获取操作者ID等属性
     */
    private void setCommonIds(Object reqObject, ManagerAccountDto account) {
        setCommonProperties(reqObject, account, null, null);
    }

    /**
     * 为请求对象设置操作者ID
     * 此方法专注于设置操作者ID，与其他方法共享通用属性设置逻辑
     * @param reqObject 请求对象，可以是BaseReq或BaseEntity的实例
     * @param account 管理员账户信息，用于获取操作者ID
     */
    private void setOperatorId(Object reqObject, ManagerAccountDto account) {
        setCommonProperties(reqObject, account, null, null);
    }

    /**
     * 为请求对象设置组织ID
     * 此方法专注于设置组织ID，通过调用setCommonProperties方法实现
     * @param reqObject 请求对象，可以是BaseReq或BaseEntity的实例
     * @param account 管理员账户信息，用于获取组织ID
     */
    private void setOrgId(Object reqObject, ManagerAccountDto account) {
        setCommonProperties(reqObject, account,
                req -> req.setOrgId(account.getOrgId()),
                entity -> entity.setOrgId(account.getOrgId())
        );
    }

    /**
     * 设置请求对象的通用属性
     * 此方法根据传入的请求对象类型，设置操作者ID、组织ID等通用属性
     * @param reqObject 请求对象，可以是BaseReq或BaseEntity的实例
     * @param account 管理员账户信息，用于获取各种通用属性值
     * @param reqConsumer 用于处理BaseReq类型对象的消费者接口，可为空
     * @param entityConsumer 用于处理BaseEntity类型对象的消费者接口，可为空
     */
    private void setCommonProperties(Object reqObject, ManagerAccountDto account, Consumer<BaseReq> reqConsumer, Consumer<BaseEntity> entityConsumer) {
        if (reqObject instanceof BaseReq) {
            BaseReq req = (BaseReq) reqObject;
            req.setOperatorId(account.getId());
            if (reqConsumer != null) {
                reqConsumer.accept(req);
            }
        } else if (reqObject instanceof BaseEntity) {
            BaseEntity req = (BaseEntity) reqObject;
            req.setOperatorId(account.getId());
            if (entityConsumer != null) {
                entityConsumer.accept(req);
            }
        } else {
            // 处理非预期类型的情况，可以记录日志或抛出异常
            throw new IllegalArgumentException("Unsupported request object type");
        }
    }

}
