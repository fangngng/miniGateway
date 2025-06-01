package com.fangngng.mini.filter;

import com.sun.net.httpserver.HttpExchange;

import java.net.http.HttpResponse;

public interface MiniFilter {

    boolean preFilter(HttpExchange httpExchange);

    void postFilter(HttpExchange httpExchange, HttpResponse<String> httpResponse);
}
