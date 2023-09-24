package com.alinesno.infra.base.gateway.formwork.bean;

import lombok.Data;

/**
 * @description 封装按天和按小时的统计维度数据
 * @author JL
 * @date 2020/12/30
 * @version v1.0.0
 */
@Data
public class CountTotalRsp implements java.io.Serializable {

    private CountRsp dayCounts;
    private CountRsp hourCounts;
    private CountRsp minCounts;

}
