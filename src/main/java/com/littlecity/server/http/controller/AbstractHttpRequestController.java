package com.littlecity.server.http.controller;

import com.littlecity.server.entity.CustomHttpRequest;
import com.littlecity.server.entity.CustomHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.io.IOException;

/**
 * @author huangxiaocheng
 * @Date 2019/9/18
 **/
public abstract class AbstractHttpRequestController implements HttpRequestController {

    @Override
    public void doService(CustomHttpRequest request, CustomHttpResponse response) throws IOException {
        if (HttpMethod.GET.equals(request.getMethod())){
            doGet(request, response);
        }

        if (HttpMethod.POST.equals(request.getMethod())){
            doPost(request, response);
        }
    }


    public abstract void doGet(CustomHttpRequest request, CustomHttpResponse response) throws IOException;

    public abstract void doPost(CustomHttpRequest request, CustomHttpResponse response) throws IOException;
}
