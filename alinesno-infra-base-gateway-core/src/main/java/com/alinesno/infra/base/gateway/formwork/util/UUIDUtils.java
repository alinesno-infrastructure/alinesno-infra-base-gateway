package com.alinesno.infra.base.gateway.formwork.util;

import java.util.UUID;

/**
 * @description
 * @author JL
 * @date 2021/09/27
 * @version v1.0.0
 */
public class UUIDUtils {

    /**
     * 获取不带-的UUID字符串
     * @return
     */
    public static String getUUIDString(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
