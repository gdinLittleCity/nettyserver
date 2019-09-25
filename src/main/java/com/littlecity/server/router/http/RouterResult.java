package com.littlecity.server.router.http;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date 2019/9/20
 **/
@Data
public class RouterResult {

    private String uri;

//    private Map<String, String> pathParams;
    private Map<String, Object> queryParams;

    private Class controller;

}
