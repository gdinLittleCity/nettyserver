package com.littlecity.server.http.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author huangxiaocheng
 * @Date 2019/9/18
 **/
public interface HttpRequestHandler {

    /**
     * http请求的业务逻辑
     * @param request
     * @param response
     */
    void doService(FullHttpRequest request, FullHttpResponse response);

}
