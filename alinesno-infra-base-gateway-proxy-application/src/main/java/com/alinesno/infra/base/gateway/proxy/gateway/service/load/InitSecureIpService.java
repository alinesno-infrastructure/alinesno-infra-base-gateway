package com.alinesno.infra.base.gateway.proxy.gateway.service.load;

import com.alinesno.infra.base.gateway.formwork.entity.SecureIp;
import com.alinesno.infra.base.gateway.formwork.service.SecureIpService;
import com.alinesno.infra.base.gateway.formwork.util.Constants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.AccountCache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description  初始化网关路IP名单配置
 * @Author JL
 * @Date 2021/09/22
 * @Version V1.0
 */
@Slf4j
@Service
public class InitSecureIpService {

    @Resource
    private SecureIpService secureIpService;

    /**
     * 第一次初始化加载
     */
    @PostConstruct
    public void initLoadSecureIp(){
        SecureIp secureIp = new SecureIp();
        secureIp.setStatus(Constants.NO);
        List<SecureIp> list = secureIpService.findAll(secureIp);
        int size = 0;
        if (CollectionUtils.isEmpty(list)){
            log.info("初始化加载IP配置共{}条", 0);
            return ;
        }

        size = list.size();

        ConcurrentHashMap<String, List<String>> ipCache =  list.stream()
                .filter(item -> item.getAccountToken() != null)
                .collect(Collectors.toMap(ip -> ip.getAccountToken(),
                        ip-> {
                            List ipList = new ArrayList<String>();
                            ipList.add(ip.getIp());
                            return ipList;
                        },
                        (List<String> v1, List<String> v2) ->{
                            v1.addAll(v2);
                            return v1;
                        },
                        ConcurrentHashMap::new)
                );

        AccountCache.putIpCaches(ipCache);

        log.info("初始化加载IP配置共{}条", size);
    }

}
