package com.littlecity.server.router.http;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
@Data
public class PathRouter {

    private Map<String, Class> pathRouter = new HashMap<>();


    public void addRouter(String path, Class controllerClass){

        pathRouter.put(path, controllerClass);

    }

    public Class matchPath(String path){
        if (StringUtils.isEmpty(path)){
            return null;
        }

       return pathRouter.get(path);
    }





}
