package com.littlecity.server.http.controller;

import com.littlecity.server.entity.CustomHttpRequest;
import com.littlecity.server.entity.CustomHttpResponse;

import java.io.IOException;

/**
 * @author huangxiaocheng
 * @Date 2019/9/18
 **/
public interface HttpRequestController {

    /**
     * http请求的业务逻辑
     * @param request
     * @param response
     */
    void doService(CustomHttpRequest request, CustomHttpResponse response) throws IOException;

}
