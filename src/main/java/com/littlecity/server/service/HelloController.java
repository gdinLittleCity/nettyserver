package com.littlecity.server.service;

import com.littlecity.server.http.controller.AbstractHttpRequestController;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author huangxiaocheng
 * @Date 2019/9/20
 **/
public class HelloController extends AbstractHttpRequestController {
    @Override
    public void doget(FullHttpRequest request, FullHttpResponse response) {
        ByteBuf content = response.content();
        content.writeBytes("hello . get.".getBytes());
    }

    @Override
    public void doPost(FullHttpRequest request, FullHttpResponse response) {
        ByteBuf content = response.content();
        content.writeBytes("hello . post.".getBytes());
    }
}
