package com.fangngng.mini.route;

import java.util.List;
import java.util.stream.Collectors;

// 组合路由定位器
public class CompositeRouteLocator implements MiniRouteLocator {
    private final List<MiniRouteLocator> delegates;

    public CompositeRouteLocator(List<MiniRouteLocator> delegates) {
        this.delegates = delegates;
    }

    @Override
    public List<MiniRoute> getRoutes() {
        return delegates.stream()
                .flatMap(locator -> locator.getRoutes().stream())
                .collect(Collectors.toList());
    }
}
