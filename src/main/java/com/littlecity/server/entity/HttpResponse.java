package com.littlecity.server.entity;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
public class HttpResponse extends DefaultFullHttpResponse {
    public HttpResponse(HttpVersion version, HttpResponseStatus status) {
        super(version, status);
    }

    public HttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
        super(version, status, content);
    }

    public HttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) {
        super(version, status, content, validateHeaders);
    }
}
