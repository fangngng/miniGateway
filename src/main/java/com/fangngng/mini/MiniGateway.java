package com.fangngng.mini;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MiniGateway {

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        Path path = Paths.get("src/main/resources/routes.properties");
        System.out.println(path.toAbsolutePath());
        properties.load(Files.newInputStream(path));

        MiniRouteLocator locator = new PropertiesRouteLocator(properties);
        MiniGatewayHandler handler = new MiniGatewayHandler(locator);

        // 创建HTTP服务器
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", handler::handle);
        server.setExecutor(null);
        server.start();

        System.out.println("Gateway started on port 8080");
    }
}
