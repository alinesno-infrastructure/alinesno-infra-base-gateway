package com.alinesno.infra.base.gateway.manage.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据权限过滤
 * 
 * @author WeiXiaoJin
 * @since 2019年9月15日 下午5:00:19
 */
@Component
@Aspect
public class DataFilterAdvice {

	// 日志记录
	private final static Logger log = LoggerFactory.getLogger(DataFilterAdvice.class);

	@Autowired
	private HttpServletRequest request;

	// 拦截指定的方法,这里指只拦截TestService.getResultData这个方法
	@Pointcut("@annotation(com.alinesno.infra.base.gateway.manage.aop.DataFilter)")
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

		DataFilter dataFilter = methodClass.getAnnotation( DataFilter.class); // .getAnnotations();
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

		// 获取当前用户
//		UserVo account = CurrentAccountJwt.getUserVo().getUser();
//
//		if (type == DataFilterRole.OPERATOR) { // 用户权限
//			if (reqObject instanceof BaseReq) { // 分页参数
//				BaseReq req = (BaseReq) reqObject;
//				req.setOperatorId(account.getUserId());
//			} else if (reqObject instanceof BaseEntity){
//				BaseEntity req = (BaseEntity) reqObject;
//				req.setOperatorId(account.getUserId());
//			}
//
//		} else if (type == DataFilterRole.DEPARTMENT) { // 部门权限
//
//		} else if (type == DataFilterRole.TENANT) { // 租户权限
//
//		} else if (type == DataFilterRole.APPLICATION) { // 应用权限
//
//		} else if (type == DataFilterRole.CUSTOM) { // 自定义权限
//		}

	}


}
