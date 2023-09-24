package com.alinesno.infra.base.gateway.formwork.util;

/**
 * @description 网关路由常量类
 * @author jianglong
 * @date 2020/05/29
 * @version v1.0.0
 */
public class RouteConstants {
    public static final String BALANCED = "balanced";
    public static final String ROUTE = "route";
    public static final String AUTHORIZE = "Authorize";
    public static final String CLIENT_ID = "clientId";
    public static final String TOKEN = "token";
    public static final String IP = "ip";
    public static final String ID = "id";
    public static final String REQUEST_ID = "requestId";
    public static final String URI = "uri";
    public static final String TRUE = "true";

    public static final String _GENKEY_ = "_genkey_";
    public static final String PATH = "Path";
    public static final String METHOD = "Method";
    public static final String HOST = "Host";
    public static final String REMOTE_ADDR = "RemoteAddr";
    public static final String HEADER = "Header";
    public static final String WEIGHT = "Weight";
    public static final String STRIP_PREFIX = "StripPrefix";
    public static final String ADD_REQUEST_PARAMETER = "AddRequestParameter";
    public static final String REWRITE_PATH = "RewritePath";

    /**
     * 负载转发根路径
     */
    public static final String PARENT_PATH = "/parent/";

    /**
     * redis统计缓存key前缀
     */
    public final static String COUNT_DAY_KEY = "FISH_GATEWAY_COUNT:DAY:";
    public final static String COUNT_HOUR_KEY = "FISH_GATEWAY_COUNT:HOUR:";
    public final static String COUNT_MIN_KEY = "FISH_GATEWAY_COUNT:MIN:";
    /**
     * redis同步版本缓存key
     */
    public final static String SYNC_VERSION_KEY = "fish_gateway_sync_version";
    /**
     * redis同步请求时间缓存key
     */
    public final static String SYNC_REQUEST_TIME_KEY = "fish_gateway_request_time";
    /**
     * redis控制队列缓存key
     */
    public final static String MONITOR_QUEUE_KEY = "fish_gateway_monitor_queue";

    /**
     * 熔断器
     */
    public static class Hystrix{
        public static final String NAME = "name";
        public static final String FALLBACK_URI = "fallbackUri";
        /**
         * 默认
         */
        public static final String DEFAULT = "default";
        public static final String DEFAULT_NAME = "Hystrix";
        public static final String DEFAULT_HYSTRIX_NAME = "fallbackcmd";
        public static final String DEFAULT_FALLBACK_URI = "forward:/fallback?routeId=";
        /**
         * 自定义
         */
        public static final String CUSTOM = "custom";
        public static final String CUSTOM_NAME = "CustomHystrix";
        public static final String CUSTOM_HYSTRIX_NAME = "customHystrix_";
        public static final String CUSTOM_FALLBACK_URI = "forward:/fallback/custom?routeId=";
    }

    /**
     * 过滤描述
     */
    public static class Secure{
        public static final String  SECURE_IP = "SecureIp";
        public static final String SECURE_CLIENT_ID = "SecureClientId";
        public static final String SECURE_TOKEN = "SecureToken";
    }

    /**
     * 限流描述
     */
    public static class Limiter{
        public static final String KEY_RESOLVER = "key-resolver";
        public static final String REPLENISH_RATE = "redis-rate-limiter.replenishRate";
        public static final String BURS_CAPACITY = "redis-rate-limiter.burstCapacity";
        public static final String REQUEST_RATE_LIMITER = "RequestRateLimiter";
        public static final String CUSTOM_REQUEST_RATE_LIMITER = "CustomRequestRateLimiter";
        public static final String HOST_ADDR_KEY_RESOLVER = "#{@hostAddrKeyResolver}";
        public static final String URI_KEY_RESOLVER = "#{@uriKeyResolver}";
        public static final String REQUEST_ID_KEY_RESOLVER = "#{@requestIdKeyResolver}";
    }

    /**
     * 鉴权描述
     */
    public static class Access{
        public static final String HEADER = "header";
        public static final String IP = RouteConstants.IP;
        public static final String PARAMETER = "parameter";
        public static final String TIME = "time";
        public static final String COOKIE = "cookie";
    }
}
