package com.alinesno.infra.base.gateway.formwork.util;

import java.util.List;

import lombok.Data;

/**
 * @description 分页实体类
 * @author jianglong
 * @date 2019/07/02
 * @version v1.0.0
 */
@Data
public class PageResult<T> {
    //总数
    private long totalNum=1;
    //下一步
    private int currentPage= Constants.CURRENT_PAGE;
    //分页数量
    private int pageSize= Constants.PAGE_SIZE;
    private List<T> lists;
    private Object data = null;
}
