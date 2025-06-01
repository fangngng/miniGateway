package com.fangngng.mini.route;

import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DynamicFileRouteLocator implements MiniRouteLocator{

    private volatile List<MiniRoute> routes;

    private final Path configPath;

    private final MiniRouteLocator routeLocator;

    private final ExecutorService watchExecutor = Executors.newSingleThreadExecutor();

    public DynamicFileRouteLocator(Path configPath, MiniRouteLocator routeLocator) {
        this.configPath = configPath;
        this.routeLocator = routeLocator;
        this.routes = routeLocator.getRoutes();
        startWatcher();
    }

    private void startWatcher(){
        watchExecutor.submit(()->{
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                configPath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true){
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> pollEvent : key.pollEvents()) {
                        if(pollEvent.context().toString().equalsIgnoreCase(configPath.getFileName().toString())){
                            refresh();
                        }
                    }
                    key.reset();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public synchronized void refresh() {
        try {
            this.routes = routeLocator.getRoutes();
            // todo 能监听文件变化么？
            System.out.println("Routes refreshed at " + System.currentTimeMillis());
        } catch (Exception e) {
            System.err.println("Failed to refresh routes: " + e.getMessage());
        }
    }

    @Override
    public List<MiniRoute> getRoutes() {
        return List.of();
    }
}
