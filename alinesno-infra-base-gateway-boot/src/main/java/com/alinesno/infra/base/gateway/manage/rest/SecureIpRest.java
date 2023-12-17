package com.alinesno.infra.base.gateway.manage.rest;

import cn.hutool.core.collection.CollectionUtil;
import com.alinesno.infra.base.gateway.core.base.BaseRest;
import com.alinesno.infra.base.gateway.core.bean.SecureIpReq;
import com.alinesno.infra.base.gateway.core.entity.Account;
import com.alinesno.infra.base.gateway.core.entity.SecureIp;
import com.alinesno.infra.base.gateway.core.service.AccountService;
import com.alinesno.infra.base.gateway.core.service.CustomRestConfigService;
import com.alinesno.infra.base.gateway.core.service.SecureIpService;
import com.alinesno.infra.base.gateway.core.util.ApiResult;
import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import com.alinesno.infra.base.gateway.core.util.UUIDUtils;
import com.alinesno.infra.base.gateway.manage.aop.DataFilter;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @description Ip管理控制器
 * @author  JL
 * @date 2020/05/28
 * @version v1.0.0
 */
@RestController
@RequestMapping("/ip")
public class SecureIpRest extends BaseRest<SecureIp> {

    @Resource
    private SecureIpService secureIpService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomRestConfigService customRestConfigService;

    @Resource
    private AccountService accountService;

    /**
     * 添加IP
     * @param secureIp
     * @return
     */
    @DataFilter
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public ApiResult add(@RequestBody SecureIp secureIp) {
        Assert.notNull(secureIp, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(secureIp.getIp()), "IP值不能为空");
        secureIp.setCreateTime(new Date());
        secureIp.setId(UUIDUtils.getUUIDString());
        secureIp.setAccountToken(getAccountToken());
        this.validate(secureIp);
        //验证注册服务是否重复
        SecureIp qSecureIp = new SecureIp();
        qSecureIp.setIp(secureIp.getIp());
        qSecureIp.setOperatorId(secureIp.getOperatorId());
        long count = secureIpService.count(qSecureIp);
        Assert.isTrue(count <= 0, "该IP已添加，请不要重复添加");
        secureIpService.save(secureIp);
        //this.setIpCacheVersion();
        customRestConfigService.publishIpConfig(secureIp.getAccountToken(), secureIp.getIp());
        return new ApiResult();
    }

    /**
     * 删除IP
     * @param ip
     * @return
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult delete(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");

        // 获取当前用户
        String operatorId = "0" ;

        SecureIp qSecureIp = new SecureIp();
        qSecureIp.setOperatorId(operatorId);
        qSecureIp.setIp(ip);
        List<SecureIp> secureIps = secureIpService.findAll(qSecureIp);
        if(CollectionUtil.isNotEmpty(secureIps)){
            SecureIp secureIp = secureIps.get(0);
            secureIpService.deleteById(secureIp.getId());
            customRestConfigService.publishIpConfig(secureIp.getAccountToken(),ip);
        }
        //this.setIpCacheVersion();

        return new ApiResult();
    }

    /**
     * 更新IP
     * @param secureIp
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public ApiResult update(@RequestBody SecureIp secureIp) {
        Assert.notNull(secureIp, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(secureIp.getIp()), "IP值不能为空");
        secureIp.setUpdateTime(new Date());
        this.validate(secureIp);
        secureIpService.update(secureIp);
        //this.setIpCacheVersion();
        customRestConfigService.publishIpConfig(secureIp.getAccountToken(), secureIp.getIp());
        return new ApiResult();
    }

    /**
     * 查询IP
     * @param ip
     * @return
     */
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult findById(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");
        return new ApiResult(secureIpService.findById(ip));
    }

    /**
     * 分页查询
     * @param secureIpReq
     * @return
     */
    @DataFilter
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult pageList(@RequestBody SecureIpReq secureIpReq){
        Assert.notNull(secureIpReq, "未获取到对象");
        int currentPage = getCurrentPage(secureIpReq.getCurrentPage());
        int pageSize = getPageSize(secureIpReq.getPageSize());
        SecureIp secureIp = new SecureIp();
        if (StringUtils.isNotBlank(secureIpReq.getIp())){
            secureIp.setIp(secureIpReq.getIp());
        }
        if (StringUtils.isNotBlank(secureIpReq.getStatus())){
            secureIp.setStatus(secureIpReq.getStatus());
        }
        this.toExample(secureIp, secureIpReq.getOperatorId());
        return new ApiResult(secureIpService.pageList(secureIp,currentPage, pageSize));
    }

    /**
     * 对IP数据进行变更后，设置redis中缓存的版本号
     */
    private void setIpCacheVersion(){
        redisTemplate.opsForHash().put(RouteConstants.SYNC_VERSION_KEY, RouteConstants.IP, String.valueOf(System.currentTimeMillis()));
    }

    private String getAccountToken(){
        // 获取当前用户

        Account result;

        Account account =  new Account();
        account.setAccountId("0");
        List<Account> accounts = accountService.findAll(account);

        if(CollectionUtil.isEmpty(accounts)){
            result = new Account();
            result.setAccountId("0");
            result.setId(UUIDUtils.getUUIDString());
            result.setAuthToken(UUIDUtils.getUUIDString());
            accountService.save(result);
            return result.getAuthToken();
        } else {
            result = accounts.get(0);
        }

        return result.getAuthToken();

    }

}
