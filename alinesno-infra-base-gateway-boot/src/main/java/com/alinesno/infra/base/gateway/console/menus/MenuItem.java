package com.alinesno.infra.base.gateway.console.menus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MenuItem {
    private String redirect;
    private String path;
    private String component;
    private boolean hidden;
    private List<MenuItem> children;
    private Meta meta;
    private String name;
    private boolean alwaysShow;

    public MenuItem(String redirect, String path, String component, boolean hidden, boolean alwaysShow, String name, Meta meta) {
        this.redirect = redirect;
        this.path = path;
        this.component = component;
        this.hidden = hidden;
        this.alwaysShow = alwaysShow;
        this.name = name;
        this.meta = meta;
    }

    public MenuItem(String path, String component, boolean hidden, String name, Meta meta) {
        this.path = path;
        this.component = component;
        this.hidden = hidden;
        this.name = name;
        this.meta = meta;
    }

    public MenuItem addChildMenuItem(MenuItem childMenuItem) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(childMenuItem);
        return this;
    }
}

