package com.alinesno.infra.base.gateway.proxy.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "alinesno.security")
public class AlinesnoSecurityProperties {
    /**
     * excludes: 列表形式的排除路径，例如 /sso/**, /logout
     */
    private List<String> excludes = new ArrayList<>();
}