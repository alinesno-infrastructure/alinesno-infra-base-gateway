package com.alinesno.infra.base.gateway.proxy.gateway.service;

import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.util.Constants;
import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import com.alinesno.infra.base.gateway.proxy.gateway.filter.ClientIdGatewayFilter;
import com.alinesno.infra.base.gateway.proxy.gateway.filter.IpGatewayFilter;
import com.alinesno.infra.base.gateway.proxy.gateway.filter.TokenGatewayFilter;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayFilterDefinition;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayPredicateDefinition;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRouteConfig;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRouteDefinition;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

/**
 * @description 将数据转换为Gateway网关需要数据格式，并返回服务路由对象
 * @author  jianglong
 * @date 2020/05/25
 * @version 1.0.0
 */
@Service
public class LoadRouteService {

    @Qualifier("uriKeyResolver")
    @Resource
    private KeyResolver uriKeyResolver;

    @Qualifier("hostAddrKeyResolver")
    @Resource
    private KeyResolver hostAddrKeyResolver;

    @Qualifier("requestIdKeyResolver")
    @Resource
    private KeyResolver requestIdKeyResolver;

    /**
     * 把传递进来的参数转换成路由对象
     * @param gwdefinition
     * @return
     */
    public RouteDefinition assembleRouteDefinition(GatewayRouteDefinition gwdefinition) {

        RouteDefinition definition = new RouteDefinition();
        definition.setId(gwdefinition.getId());
        definition.setOrder(gwdefinition.getOrder());
        //设置断言
        List<PredicateDefinition> pdList=new ArrayList<>();
        List<GatewayPredicateDefinition> gatewayPredicateDefinitionList=gwdefinition.getPredicates();
        for (GatewayPredicateDefinition gpDefinition: gatewayPredicateDefinitionList) {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setArgs(gpDefinition.getArgs());
            predicate.setName(gpDefinition.getName());
            pdList.add(predicate);
        }
        definition.setPredicates(pdList);

        //设置过滤器
        List<FilterDefinition> filters = new ArrayList();
        List<GatewayFilterDefinition> gatewayFilters = gwdefinition.getFilters();
        for(GatewayFilterDefinition filterDefinition : gatewayFilters){
            FilterDefinition filter = new FilterDefinition();
            filter.setName(filterDefinition.getName());
            filter.setArgs(filterDefinition.getArgs());
            filters.add(filter);
        }
        definition.setFilters(filters);
        definition.setUri(this.getURI(gwdefinition.getUri()));

        return definition;
    }

    /**
     * 封装网关路由参数，返回RouteDefinition
     * @param r
     * @return
     */
    public RouteDefinition loadRouteDefinition(Route r){
        RouteDefinition definition = new RouteDefinition();
        definition.setId(r.getId());
        definition.setOrder(0);
        List<PredicateDefinition> predicates = new ArrayList<>();
        definition.setPredicates(predicates);
        List<FilterDefinition> filters = new ArrayList<>();
        definition.setFilters(filters);
        definition.setUri(this.getURI(r.getUri()));
        predicates.add(setPredicateDefinition("Account"));
        //权重（注意此处，当权重值小于等于0，则表示无流量流入，不创建网关）
        if (r.getWeight() != null && r.getWeight() > 0) {
            predicates.add(setPredicateDefinition(RouteConstants.WEIGHT, r.getWeightName(), String.valueOf(r.getWeight())));
        }
        //断言路径
        if (StringUtils.isNotBlank(r.getPath())) {
            predicates.add(setPredicateDefinition(RouteConstants.PATH, r.getPath()));
        }
        //请求模式
        if (StringUtils.isNotBlank(r.getMethod())) {
            predicates.add(setPredicateDefinition(RouteConstants.METHOD, r.getMethod()));
        }
        //断言主机
        if (StringUtils.isNotBlank(r.getHost())) {
            String[] parameters = r.getHost().split(Constants.SEPARATOR_SIGN);
            predicates.add(setPredicateDefinition(RouteConstants.HOST, parameters));
        }
        //断言远程地址
        if (StringUtils.isNotBlank(r.getRemoteAddr())) {
            String[] parameters = r.getRemoteAddr().split(Constants.SEPARATOR_SIGN);
            predicates.add(setPredicateDefinition(RouteConstants.REMOTE_ADDR, parameters));
        }
        //断言Header
        if (StringUtils.isNotBlank(r.getHeader())) {
            String[] parametersOrigin = r.getHeader().split(Constants.SEPARATOR_SIGN);
            String[] parameters = Arrays.stream(parametersOrigin).map(item -> item.trim()).toArray(String[]::new);
            predicates.add(setPredicateDefinition(RouteConstants.HEADER, parameters));
        }
        //URL截取方式
        if (r.getStripPrefix() != null && r.getStripPrefix() > 0) {
            filters.add(setFilterDefinition(RouteConstants.STRIP_PREFIX, String.valueOf(r.getStripPrefix())));
        }
        //请求参数
        if (StringUtils.isNotBlank(r.getRequestParameter())) {
            String[] parameters = r.getRequestParameter().split(Constants.SEPARATOR_SIGN);
            filters.add(setFilterDefinition(RouteConstants.ADD_REQUEST_PARAMETER, parameters));
        }
        //重写Path路径
        if (StringUtils.isNotBlank(r.getRewritePath())) {
            String[] parametersOrigin = r.getRewritePath().split(Constants.SEPARATOR_SIGN);
            String[] parameters = Arrays.stream(parametersOrigin).map(item -> item.trim()).toArray(String[]::new);
            filters.add(setFilterDefinition(RouteConstants.REWRITE_PATH, parameters));
        }
        //鉴权
        if (StringUtils.isNotBlank(r.getFilterAuthorizeName())){
            filters.add(setFilterDefinition(RouteConstants.AUTHORIZE, RouteConstants.TRUE));
        }
        //过滤器,id,ip,token
        if (StringUtils.isNotBlank(r.getFilterGatewaName())) {
            String names = r.getFilterGatewaName();
            if (names.contains(RouteConstants.IP)) {
                filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_IP, RouteConstants.TRUE));
            }
            if (names.contains(RouteConstants.ID)) {
                filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_CLIENT_ID, RouteConstants.TRUE));
            }
            if (names.contains(RouteConstants.TOKEN)) {
                filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_TOKEN, RouteConstants.TRUE));
            }
        }
        //熔断
        if (StringUtils.isNotBlank(r.getFilterHystrixName())) {
            Map<String, String> args = new LinkedHashMap<>();
            FilterDefinition filter = new FilterDefinition();
            if (r.getFilterHystrixName().equals(RouteConstants.Hystrix.DEFAULT)) {
                filter.setName(RouteConstants.Hystrix.DEFAULT_NAME);
                args.put(RouteConstants.Hystrix.NAME, RouteConstants.Hystrix.DEFAULT_HYSTRIX_NAME);
                args.put(RouteConstants.Hystrix.FALLBACK_URI, RouteConstants.Hystrix.DEFAULT_FALLBACK_URI + r.getId());
            } else {
                filter.setName(RouteConstants.Hystrix.CUSTOM_NAME);
                //自定义名称必需保持业务的唯一性，否则所有自定义熔断将共用一个配置（用id是因为值短，只要能保证hystrixName不重复就行）
                args.put(RouteConstants.Hystrix.NAME, RouteConstants.Hystrix.CUSTOM_HYSTRIX_NAME + r.getId());
                args.put(RouteConstants.Hystrix.FALLBACK_URI, RouteConstants.Hystrix.CUSTOM_FALLBACK_URI + r.getId());
            }
            filter.setArgs(args);
            filters.add(filter);
        }
        //限流
        if (StringUtils.isNotBlank(r.getFilterRateLimiterName())) {
            String name = r.getFilterRateLimiterName();
            Map<String, String> args = new LinkedHashMap<>();
            FilterDefinition filter = new FilterDefinition();
            filter.setName(RouteConstants.Limiter.CUSTOM_REQUEST_RATE_LIMITER);
            if (name.equals(RouteConstants.IP)) {
                args.put(RouteConstants.Limiter.KEY_RESOLVER,  RouteConstants.Limiter.HOST_ADDR_KEY_RESOLVER);
            } else if (name.equals(RouteConstants.URI)) {
                args.put(RouteConstants.Limiter.KEY_RESOLVER, RouteConstants.Limiter.URI_KEY_RESOLVER);
            } else if (name.equals(RouteConstants.REQUEST_ID)) {
                args.put(RouteConstants.Limiter.KEY_RESOLVER, RouteConstants.Limiter.REQUEST_ID_KEY_RESOLVER);
            }
            args.put(RouteConstants.Limiter.REPLENISH_RATE , r.getReplenishRate() + "");
            args.put(RouteConstants.Limiter.BURS_CAPACITY, r.getBurstCapacity() + "");
            filter.setArgs(args);
            filters.add(filter);
        }
        return definition;
    }

    /**
     * 封装Predicate属性值，返回PredicateDefinition对象
     * @param name
     * @param values
     * @return
     */
    private PredicateDefinition setPredicateDefinition(String name, String ... values){
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> args = new HashMap<String, String>();
        predicate.setName(name);
        int i=0;
        for (String value : values){
            args.put(RouteConstants._GENKEY_ + i, value);
            i++;
        }
        predicate.setArgs(args);
        return predicate;
    }

    /**
     * 封装Filter属性值，返回FilterDefinition对象
     * @param name
     * @param values
     * @return
     */
    private FilterDefinition setFilterDefinition(String name, String ... values){
        FilterDefinition filter = new FilterDefinition();
        filter.setName(name);
        Map<String, String> args = new LinkedHashMap<>();
        int i=0;
        for (String value : values){
            args.put(RouteConstants._GENKEY_ + i, value);
            i++;
        }
        filter.setArgs(args);
        return filter;
    }

    /**
     * 封状URI
     * @param uriStr
     * @return
     */
    private URI getURI(String uriStr){
        URI uri ;
        if(uriStr.startsWith(Constants.HTTP)){
            uri = UriComponentsBuilder.fromHttpUrl(uriStr).build().toUri();
        }else{
            // uri为lb://consumer-service
            uri = URI.create(uriStr);
        }
        return uri;
    }

    /**
     * 封装网关路由对象，用于系统执行config初始化加载时使用
     * @param r     自定义服务路由对象
     * @return
     */
    public GatewayRouteConfig loadRouteConfig(Route r){
        GatewayRouteConfig config = new GatewayRouteConfig();
        config.setId(r.getId());
        config.setOrder(0);
        config.setUri(r.getUri());
        //断言路径
        if (StringUtils.isNotBlank(r.getPath())) {
            config.setPath(r.getPath());
        }
        //请求模式
        if (StringUtils.isNotBlank(r.getMethod())) {
            config.setMethod(r.getMethod());
        }
        //断言主机
        if (StringUtils.isNotBlank(r.getHost())) {
            config.setHost(r.getHost());
        }
        //断言远程地址
        if (StringUtils.isNotBlank(r.getRemoteAddr())) {
            config.setRemoteAddr(r.getRemoteAddr());
        }
        //断言截取
        if (r.getStripPrefix() != null && r.getStripPrefix() > 0) {
            config.setStripPrefix(r.getStripPrefix().intValue());
        }
        //请求参数
        if (StringUtils.isNotBlank(r.getRequestParameter())) {
            String[] parameters = r.getRequestParameter().split(Constants.SEPARATOR_SIGN);
            config.setRequestParameterName(parameters[0]);
            config.setRequestParameterValue(parameters[1]);
        }
        //重写Path路径
        if (StringUtils.isNotBlank(r.getRewritePath())) {
            String[] parameters = r.getRewritePath().split(Constants.SEPARATOR_SIGN);
            config.setRewritePathName(parameters[0]);
            config.setRewritePathValue(parameters[1]);
        }
        //过滤器,id,ip,token
        if (StringUtils.isNotBlank(r.getFilterGatewaName())) {
            List<GatewayFilter> filters = new ArrayList<>();
            String names = r.getFilterGatewaName();
            if (names.contains(RouteConstants.IP)) {
                filters.add(new IpGatewayFilter(r.getId()));
            }
            if (names.contains(RouteConstants.ID)) {
                filters.add(new ClientIdGatewayFilter(r.getId()));
            }
            if (names.contains(RouteConstants.TOKEN)) {
                filters.add(new TokenGatewayFilter(r.getId()));
            }
            config.setGatewayFilter(filters.toArray(new GatewayFilter[]{}));
        }
        //熔断
        if (StringUtils.isNotBlank(r.getFilterHystrixName())) {
            //默认内置熔断
            if (r.getFilterHystrixName().equals(RouteConstants.Hystrix.DEFAULT)) {
                config.setHystrixName(RouteConstants.Hystrix.DEFAULT_HYSTRIX_NAME);
                config.setFallbackUri(RouteConstants.Hystrix.DEFAULT_FALLBACK_URI + r.getId());
                //自定义熔断
            } else {
                //自定义名称必需保持业务的唯一性，否则所有自定义熔断将共用一个配置（用id是因为值短，只要能保证hystrixName不重复就行）
                config.setHystrixName(RouteConstants.Hystrix.CUSTOM_HYSTRIX_NAME + r.getId());
                config.setFallbackUri(RouteConstants.Hystrix.CUSTOM_FALLBACK_URI + r.getId());
                //添加自定义熔断器
                HystrixObservableCommand.Setter setter = HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(config.getHystrixName()));
                setter.andCommandKey(HystrixCommandKey.Factory.asKey(config.getHystrixName()));
                HystrixCommandProperties.Setter propertiesSetter = HystrixCommandProperties.Setter();
                //启用回调
                propertiesSetter.withFallbackEnabled(true);
                //启用超时检测
                propertiesSetter.withExecutionTimeoutEnabled(true);
                //设置超时时长
                propertiesSetter.withExecutionTimeoutInMilliseconds(r.getFallbackTimeout().intValue());
                setter.andCommandPropertiesDefaults(propertiesSetter);
                config.setSetter(setter);
            }
        }
        //限流
        if (StringUtils.isNotBlank(r.getFilterRateLimiterName())) {
            String name = r.getFilterRateLimiterName();
            KeyResolver keyResolver = null;
            if (name.equals(RouteConstants.IP)) {
                keyResolver = hostAddrKeyResolver;
            } else if (name.equals(RouteConstants.URI)) {
                keyResolver = uriKeyResolver;
            } else if (name.equals(RouteConstants.REQUEST_ID)) {
                keyResolver = requestIdKeyResolver;
            }
            config.setKeyResolver(keyResolver);
            config.setReplenishRate(r.getReplenishRate());
            config.setBurstCapacity(r.getBurstCapacity());
        }
        //鉴权
        if (StringUtils.isNotBlank(r.getFilterAuthorizeName())){
            config.setAuthorize(true);
        }
        return config;
    }


}
