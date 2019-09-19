package com.littlecity.server.router.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
public class PathRouter {

    private Map<String, Class> pathRouter = new HashMap<>();


    public void addRouter(String path, Class controllerClass){

        pathRouter.put(path, controllerClass);

    }

    public boolean matchPath(String path){
        if (pathRouter.get(path) != null){
            return true;
        }

        return false;
    }


}
