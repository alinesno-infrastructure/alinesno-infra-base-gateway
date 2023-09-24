package com.alinesno.infra.base.gateway.formwork.util;

import org.springframework.http.HttpStatus;

/**
 * @description 常量类
 * @author jianglong
 * @date 2019/07/01
 * @version v1.0.0
 */
public class Constants {

    /**
     * 应用数据库字段：0正常，1无效
     */
    public static final String YES = "0";
    public static final String NO = "1";

    /**
     * 应用前后端字段：0表示未获取响应结果（异常），1表示有响应结果（正常），-1表示未登录或无权限, 2表示告警
     */
    public static final String SUCCESS = HttpStatus.OK.value() + "";
    public static final String FAILED = HttpStatus.INTERNAL_SERVER_ERROR.value() + "";
    public static final String NOT_LOGIN = "-1";
    public static final String ALARM = "2";

    public static final String ENCODING = "UTF-8";
    public static final String HTTP_REQUEST = "request";
    public static final String HTTP_RESPONSE = "response";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    public static final String SEPARATOR_SIGN = ",";
    public static final String NULL = "null";
    public static final String HTTP = "http";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    /**
     * 下一步,默认从1开始
     */
    public static final int CURRENT_PAGE=1;
    /**
     * 分页记录行大小
     */
    public static final int PAGE_SIZE=10;

    /**
     * 时间常量
     */
    public final static String DAY = "day";
    public final static String HOUR = "hour";
    public final static String MIN = "min";
    /**
     * 日期格式化常量
     */
    public final static String DATE_FORMAT_DAY = "yyyyMMdd";
    public final static String DATE_FORMAT_HOUR = "HH";
    public final static String DATE_FORMAT_MIN = "mm";
    public final static String YYYYMMDD = DATE_FORMAT_DAY;
    public final static String YYYYMMDDHH = "yyyyMMddHH";
    public final static String YYYYMMDDHHMM = "yyyyMMddHHmm";

    /**
     * 动作标识：update更新，remove删除
     */
    public final static String UPDATE = "update";
    public final static String REMOVE = "remove";

    /**
     * 路由id后缀长度 (转发路由)
     */
    public final static int ID_SIZE = 6;

    /**
     * 用户验证头部
     */
    public final static String AUTH_GATEWAY = "X-Auth-Gateway";
}
