package com.fangngng.mini;

import com.fangngng.mini.route.*;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MiniGateway {

    public static void main(String[] args) throws Exception {


        // 1. 静态Properties配置
        Properties props = new Properties();
        props.load(Files.newInputStream(Paths.get("src/main/resources/routes.properties")));
        System.out.println(Paths.get("src/main/resources/routes.properties").toAbsolutePath());
        MiniRouteLocator propsLocator = new PropertiesRouteLocator(props);

        // 2. YAML配置
        InputStream yamlStream = Files.newInputStream(Paths.get("src/main/resources/routes.yaml"));
        System.out.println(Paths.get("src/main/resources/routes.yml").toAbsolutePath());
        MiniRouteLocator yamlLocator = new YamlRouteLocator(yamlStream);

        // 3. 动态文件配置
        Path dynamicConfigPath = Paths.get("src/main/resources/dynamic-routes.yml");
        System.out.println(Paths.get("src/main/resources/dynamic-routes.yml").toAbsolutePath());
        MiniRouteLocator dynamicLocator = new DynamicFileRouteLocator(
                dynamicConfigPath,
                new YamlRouteLocator(Files.newInputStream(dynamicConfigPath))
        );

        // 4. 组合多个配置源（后者优先级更高）
        CompositeRouteLocator compositeLocator = new CompositeRouteLocator(
                Arrays.asList(propsLocator, yamlLocator, dynamicLocator)
        );

        MiniGatewayHandler handler = new MiniGatewayHandler(compositeLocator);

        // 创建HTTP服务器
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", handler::handle);
        server.setExecutor(null);
        server.start();

        System.out.println("Gateway started on port 8080");
    }


}
