package com.fangngng.mini;

import com.sun.net.httpserver.HttpExchange;

import java.net.http.HttpResponse;
import java.util.List;

public class AuthFilter implements MiniFilter{
    @Override
    public boolean preFilter(HttpExchange httpExchange) {
        String authorization = httpExchange.getRequestHeaders().getFirst("Authorization");
        return isAuth(authorization);
    }

    private boolean isAuth(String token){
        return true;
    }

    @Override
    public void postFilter(HttpExchange httpExchange, HttpResponse<String> httpResponse) {

    }
}
