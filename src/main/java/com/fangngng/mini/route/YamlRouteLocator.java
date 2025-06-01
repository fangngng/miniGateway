package com.fangngng.mini.route;

import com.fangngng.mini.filter.AuthFilter;
import com.fangngng.mini.filter.MiniFilter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class YamlRouteLocator implements MiniRouteLocator{

    private List<MiniRoute> routeList;

    public YamlRouteLocator(InputStream yamlStream){
        Yaml yaml = new Yaml();
        Map<String, Object> configs = yaml.load(yamlStream);
        this.routeList = paresConfig(configs);
    }

    @Override
    public List<MiniRoute> getRoutes() {
        return Collections.unmodifiableList(routeList);
    }

    private List<MiniRoute> paresConfig(Map<String, Object> configs){
        List<Map<String, Object>> routeConfigs = (List<Map<String, Object>>) configs.get("routes");
        if(routeConfigs.isEmpty()){
            return new ArrayList<>();
        }
        return routeConfigs.stream()
                .map(this::buildRouteFromYaml)
                .collect(Collectors.toList());
    }

    private MiniRoute buildRouteFromYaml(Map<String, Object> config) {
        MiniRoute route = new MiniRoute();
        route.setId((String) config.get("id"));
        route.setPath((String) config.get("path"));
        route.setUri((String) config.get("uri"));

        if (config.containsKey("filters")) {
            List<String> filterNames = (List<String>) config.get("filters");
            List<MiniFilter> filters = filterNames.stream()
                    .map(this::createFilter)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            route.setFilterList(filters);
        } else {
            route.setFilterList(Collections.emptyList());
        }

        return route;
    }

    private MiniFilter createFilter(String filterName) {
        try {
            // 简单实现，实际可以使用Spring等DI容器
            switch (filterName) {
                case "AuthFilter": return new AuthFilter();
//                case "LoggingFilter": return new LoggingFilter();
//                case "RateLimitFilter": return new RateLimitFilter();
                default:
                    System.err.println("Unknown filter: " + filterName);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to create filter " + filterName + ": " + e.getMessage());
            return null;
        }
    }

    public void refresh(InputStream yamlStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(yamlStream);
        this.routeList = paresConfig(config);
    }
}
