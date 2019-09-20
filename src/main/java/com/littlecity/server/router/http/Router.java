package com.littlecity.server.router.http;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.http.controller.AbstractHttpRequestController;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
@Slf4j
public class Router {

    private final Map<HttpMethod, PathRouter> router = new HashMap<>();

    private final PathRouter anyMethodRouter = new PathRouter();

    private Class<? extends AbstractHttpRequestController> notFound;

    public Router addRouter(HttpMethod method, String path, Class controllerClass){
        if (anyMatchRouter(method, path)){
            return this;
        }
        if (method == null){
            anyMethodRouter.addRouter(path, controllerClass);
            return this;
        }

        PathRouter pathRouter = router.get(method);
        if (pathRouter == null) {
            pathRouter = new PathRouter();
        }
        pathRouter.addRouter(path, controllerClass);
        router.put(method, pathRouter);

        return this;
    }

    public void logRouterList(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        router.entrySet().forEach(entry -> {
            sb.append("\"")
                    .append(entry.getKey().name())
                    .append("\"")
                    .append(":")
                    .append(JSON.toJSONString(entry.getValue()))
                    .append(",");
        });
        String str = sb.toString();
        str = str.substring(0, str.lastIndexOf(","));

        str = str + "}";

        log.info("router list: anyMethodRouter:{}, router:{}", JSON.toJSONString(anyMethodRouter), str);
    }

    public boolean anyMatchRouter(HttpMethod method, String path){

        if (anyMethodRouter.matchPath(path) != null){
            return true;
        }

        return false;
    }


    public RouterResult route(HttpMethod method, String uri){
        log.info("router method:{}, uri:{}", method.name(), uri);
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        String path = decoder.path();
        log.info("decode path:{} , parameters:{}", path, decoder.parameters());

        RouterResult routerResult = new RouterResult();
        Class controller ;
        // 获取 请求处理的controller
        if (method == null){
            controller = anyMethodRouter.matchPath(path);
        } else {
            PathRouter pathRouter = router.get(method);

            controller =  pathRouter == null ? null : pathRouter.matchPath(path);
        }

        if (controller == null ){
            if (notFound != null) {
                controller = notFound;
            } else {
                controller = NotFoundController.class;
            }
        }

        routerResult.setController(controller);
        routerResult.setQueryParams(decoder.parameters());
        routerResult.setUri(uri);

        return routerResult;
    }


    public Router get(String path, Class controller){
        return addRouter(HttpMethod.GET, path, controller);
    }


    public Router post(String path, Class controller){
        return addRouter(HttpMethod.POST, path, controller);
    }

    public Router notFound(Class<? extends AbstractHttpRequestController> controller){
        this.notFound = controller;
        return this;
    }

    /*public static void main(String[] args) {
        Router router = new Router();
        RouterResult route = router.route(HttpMethod.POST, "/getName?name=123,21&age=12");


    }*/

}
