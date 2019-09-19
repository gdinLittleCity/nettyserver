package com.littlecity.server.router.http;

import io.netty.handler.codec.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
public class Router {

    private final Map<HttpMethod, PathRouter> router = new HashMap<>();

    private final PathRouter anyMethodRouter = new PathRouter();

    public Router addRouter(HttpMethod method, String path, Class controllerClass){
        if (anyMatchRouter(method, path)){
            return this;
        }

        PathRouter pathRouter = router.get(method);
        if (pathRouter == null) {
            pathRouter = new PathRouter();
        }
        pathRouter.addRouter(path, controllerClass);

        return this;
    }

    public boolean anyMatchRouter(HttpMethod method, String path){

        if (anyMethodRouter.matchPath(path)){
            return true;
        }

        return false;
    }

    public boolean methodMatchRouter(HttpMethod method, String path){
        PathRouter pathRouter = router.get(method);

        if (pathRouter.matchPath(path)){
            return true;
        }

        return false;
    }

}
