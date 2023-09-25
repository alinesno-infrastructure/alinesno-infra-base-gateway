package com.alinesno.infra.base.gateway.console.menus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Meta {
    private boolean noCache;
    private String icon;
    private String link;
    private String title;
}
