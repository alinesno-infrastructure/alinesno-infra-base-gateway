package com.alinesno.infra.base.gateway.console.controller;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.base.gateway.console.dto.LoginBody;
import com.alinesno.infra.base.gateway.console.menus.MenuItem;
import com.alinesno.infra.base.gateway.console.menus.Meta;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class CommonLoginController {

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = UUID.randomUUID().toString() ;
        ajax.put(TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {

        Map<String, Object> data = new HashMap<>();
        // 将数据填充到data中...
        data.put("permissions", new String[]{"*:*:*"});

        Map<String, Object> user = new HashMap<>();
        user.put("createBy", "admin");
        user.put("createTime", "2023-04-23 16:11:38");
        user.put("updateBy", null);
        user.put("updateTime", null);
        user.put("remark", "管理员");
        user.put("userId", 1);
        user.put("deptId", 103);
        user.put("userName", "admin");
        user.put("nickName", "AIP技术团队");
        user.put("email", "aip-team@163.com");
        user.put("phonenumber", "15888888888");
        user.put("sex", "1");
        user.put("avatar", "");
        user.put("password", "");
        user.put("status", "0");
        user.put("delFlag", "0");
        user.put("loginIp", "");
        user.put("loginDate", "2023-09-21T16:54:12.000+08:00");

        Map<String, Object> dept = new HashMap<>();
        dept.put("createBy", null);
        dept.put("createTime", null);
        dept.put("updateBy", null);
        dept.put("updateTime", null);
        dept.put("remark", null);
        dept.put("deptId", 103);
        dept.put("parentId", 101);
        dept.put("ancestors", "0,100,101");
        dept.put("deptName", "研发部门");
        dept.put("orderNum", 1);
        dept.put("leader", "AIP技术团队");
        dept.put("phone", null);
        dept.put("email", null);
        dept.put("status", "0");
        dept.put("delFlag", null);
        dept.put("parentName", null);
        dept.put("children", new Object[]{});

        user.put("dept", dept);

        Map<String, Object> role = new HashMap<>();
        role.put("createBy", null);
        role.put("createTime", null);
        role.put("updateBy", null);
        role.put("updateTime", null);
        role.put("remark", null);
        role.put("roleId", 1);
        role.put("roleName", "超级管理员");
        role.put("roleKey", "admin");
        role.put("roleSort", 1);
        role.put("dataScope", "1");
        role.put("menuCheckStrictly", false);
        role.put("deptCheckStrictly", false);
        role.put("status", "0");
        role.put("delFlag", null);
        role.put("flag", false);
        role.put("menuIds", null);
        role.put("deptIds", null);
        role.put("permissions", null);
        role.put("admin", true);

        user.put("roles", new Object[]{role});

        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", user.get("roles"));
        ajax.put("permissions", data.get("permissions"));

        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        MenuItem menuItem1 = new MenuItem("noRedirect", "/dashboard", "Layout", false, true, "Dashboard", new Meta(false, "tool", null, "概览"));
        MenuItem childMenuItem = new MenuItem("index", "index", false, "Index", new Meta(false, "build", null, "仪盘表"));
        menuItem1.setChildren(List.of(childMenuItem));

        MenuItem menuItem2 = new MenuItem("noRedirect", "/gateway", "Layout", false, true, "System", new Meta(false, "system", null, "网关功能"));
        MenuItem menuItem3 = new MenuItem("loadBalanced", "gateway/loadBalanced", false, "LoadBalanced", new Meta(false, "user", null, "负载管理"));
        MenuItem menuItem4 = new MenuItem("gatewayList", "gateway/gatewayList", false, "GatewayList", new Meta(false, "peoples", null, "服务管理"));
        MenuItem menuItem5 = new MenuItem("clientList", "gateway/clientList", false, "ClientList", new Meta(false, "tree-table", null, "客户端"));
        MenuItem menuItem6 = new MenuItem("ipList", "gateway/ipList", false, "IpList", new Meta(false, "tree", null, "IP名单"));
        menuItem2.setChildren(List.of(menuItem3, menuItem5, menuItem6, menuItem4));

        MenuItem menuItem7 = new MenuItem("noRedirect", "/monitor", "Layout", false, true, "Monitor", new Meta(false, "monitor", null, "系统监控"));
        MenuItem menuItem8 = new MenuItem("apiCount", "monitor/apiCount", false, "ApiCount", new Meta(false, "online", null, "接口统计"));
        MenuItem menuItem9 = new MenuItem("apiMonitor", "monitor/apiMonitor", false, "ApiMonitor", new Meta(false, "job", null, "接口监控"));
        MenuItem menuItem10 = new MenuItem("apiDoc", "monitor/apiDoc", false, "ApiDoc", new Meta(false, "druid", null, "接口文档"));
        menuItem7.setChildren(List.of(menuItem8, menuItem9, menuItem10));

        // Create the final list of menu items
        List<MenuItem> menuItems = List.of(menuItem1, menuItem2, menuItem7);

        return AjaxResult.success(menuItems) ;
    }
}
