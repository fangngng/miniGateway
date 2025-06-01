package com.fangngng.mini.route;

import com.fangngng.mini.filter.AuthFilter;
import com.fangngng.mini.filter.MiniFilter;

import java.util.*;
import java.util.stream.Collectors;

public class PropertiesRouteLocator implements MiniRouteLocator {

    private List<MiniRoute> routeList;

    @Override
    public List<MiniRoute> getRoutes() {
        return this.routeList;
    }

    public PropertiesRouteLocator(Properties properties) {
        // read route from properties
        Map<String, MiniRoute> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = String.valueOf(entry.getKey());
            if(!key.startsWith("routes")){
                continue;
            }

            String value = String.valueOf(entry.getValue());
            String[] split = key.split("\\.");
            String id = split[1];
            String p = split[2];

            map.putIfAbsent(id, new MiniRoute());
            if(p.equalsIgnoreCase("id")) {
                map.get(id).setId(value);
            }
            if(p.equalsIgnoreCase("path")) {
                map.get(id).setPath(value);
            }
            if(p.equalsIgnoreCase("uri")) {
                map.get(id).setUri(value);
            }
            if(p.equalsIgnoreCase("filters")){
                List<MiniFilter> routes = Arrays.stream(value.split(","))
                        .map(String::trim)
                        .map(this::createRoute)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                map.get(id).setFilterList(routes);
            }
        }

        this.routeList = new ArrayList<>(map.values());
    }

    private MiniFilter createRoute(String name){
        switch (name){
            case "AuthFilter": return new AuthFilter();
            default:
                return null;
        }
    }
}
