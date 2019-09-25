package com.littlecity.server.router.http;

import com.littlecity.server.entity.CustomHttpRequest;
import com.littlecity.server.entity.CustomHttpResponse;
import com.littlecity.server.http.controller.AbstractHttpRequestController;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author huangxiaocheng
 * @Date 2019/9/20
 **/
public class NotFoundController extends AbstractHttpRequestController {
    @Override
    public void doGet(CustomHttpRequest request, CustomHttpResponse response) {
        doPost(request, response);
    }

    @Override
    public void doPost(CustomHttpRequest request, CustomHttpResponse response) {
        response.setStatus(HttpResponseStatus.NOT_FOUND);

        ByteBuf content = response.content();
        content.writeBytes("resources not found.".getBytes());
    }
}
