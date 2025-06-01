package com.fangngng.mini;

import com.fangngng.mini.filter.MiniFilter;
import com.fangngng.mini.route.MiniRoute;
import com.fangngng.mini.route.MiniRouteLocator;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class MiniGatewayHandler {

    private MiniRouteLocator routeLocator;

    private HttpClient httpClient;

    public MiniGatewayHandler(MiniRouteLocator routeLocator) {
        this.routeLocator = routeLocator;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void handle(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();

            // 查找路由
            Optional<MiniRoute> first = routeLocator.getRoutes().stream().filter(route -> path.startsWith(route.getPath()))
                    .findFirst();

            if (!first.isPresent()) {
                httpExchange.sendResponseHeaders(404, 0);
                httpExchange.close();
                return;
            }

            MiniRoute matchRoute = first.get();

            // 前置过滤器
            for (MiniFilter miniFilter : matchRoute.getFilterList()) {
                if (!miniFilter.preFilter(httpExchange)) {
                    httpExchange.sendResponseHeaders(403, 0);
                    httpExchange.close();
                    return;
                }
            }

            // 转发请求
            String backendUrl = buildBackendUrl(matchRoute.getUri(), path);
            HttpRequest request = buildRequest(httpExchange, backendUrl);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 后置过滤器
            for (MiniFilter miniFilter : matchRoute.getFilterList()) {
                miniFilter.postFilter(httpExchange, response);
            }

            // 返回响应
            httpExchange.getResponseHeaders().putAll(response.headers().map());
            httpExchange.sendResponseHeaders(response.statusCode(), response.body().length());
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.body().getBytes(StandardCharsets.UTF_8));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String buildBackendUrl(String uri, String path){
        return uri + path;
    }

    private HttpRequest buildRequest(HttpExchange exchange, String url){
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url))
                .method(exchange.getRequestMethod(), HttpRequest.BodyPublishers.ofInputStream(exchange::getRequestBody));

        // 只复制允许的头字段
        exchange.getRequestHeaders().forEach((headerName, headerValues) -> {
            if (!isRestrictedHeader(headerName)) {
                headerValues.forEach(headerValue ->
                        builder.header(headerName, headerValue));
            }
        });
//        exchange.getRequestHeaders().forEach((k,v)->v.forEach(val -> builder.header(k, val)));

        return builder.build();
    }

    // 检查是否是受限制的头字段
    private boolean isRestrictedHeader(String headerName) {
        return headerName.equalsIgnoreCase("Connection")
                || headerName.equalsIgnoreCase("Content-Length")
                || headerName.equalsIgnoreCase("Expect")
                || headerName.equalsIgnoreCase("Host")
                || headerName.equalsIgnoreCase("Upgrade");
    }
}
