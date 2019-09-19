package com.littlecity.server.http.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

/**
 * @author huangxiaocheng
 * @Date 2019/9/18
 **/
public abstract class AbstractHttpRequestController implements HttpRequestController {

    @Override
    public void doService(FullHttpRequest request, FullHttpResponse response) {
        if (HttpMethod.GET.equals(request.getMethod())){
            doget(request, response);
        }

        if (HttpMethod.POST.equals(request.getMethod())){
            doPost(request, response);
        }
    }


    public abstract void doget(FullHttpRequest request, FullHttpResponse response);

    public abstract void doPost(FullHttpRequest request, FullHttpResponse response);
}
