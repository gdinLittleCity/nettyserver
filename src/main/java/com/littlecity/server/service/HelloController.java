package com.littlecity.server.service;

import com.littlecity.server.entity.CustomHttpRequest;
import com.littlecity.server.entity.CustomHttpResponse;
import com.littlecity.server.http.controller.AbstractHttpRequestController;
import io.netty.buffer.ByteBuf;

/**
 * @author huangxiaocheng
 * @Date 2019/9/20
 **/
public class HelloController extends AbstractHttpRequestController {
    @Override
    public void doGet(CustomHttpRequest request, CustomHttpResponse response) {
        ByteBuf content = response.content();
        content.writeBytes("hello . get.".getBytes());
    }

    @Override
    public void doPost(CustomHttpRequest request, CustomHttpResponse response) {
        ByteBuf content = response.content();
        content.writeBytes("hello . post.".getBytes());
    }
}
