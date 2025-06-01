package com.fangngng.mini.route;

import com.fangngng.mini.filter.MiniFilter;

import java.util.List;

public class MiniRoute {

    private String id;

    private String path;

    private String uri;

    private List<MiniFilter> filterList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<MiniFilter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<MiniFilter> filterList) {
        this.filterList = filterList;
    }
}
