package com.alinesno.infra.base.gateway.formwork.bean;

import java.util.List;

import lombok.Data;

/**
 * @description
 * @author jianglong
 * @date 2020/07/07
 * @version v1.0.0
 */
@Data
public class CountRsp implements java.io.Serializable {

    private List<String> dates;
    private List<CountData> datas;
    private List<Integer> counts;

    @Data
    public static class CountData{
        private String name;
        private String routeId;
        private Integer [] counts;
    }
}
