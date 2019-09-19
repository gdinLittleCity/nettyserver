package com.littlecity.server.entity;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Collections;
import java.util.List;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
public class HttpRequest extends DefaultFullHttpRequest {
    public HttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        super(httpVersion, method, uri);
    }

    public HttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
        super(httpVersion, method, uri, content);
    }

    public HttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, boolean validateHeaders) {
        super(httpVersion, method, uri, content, validateHeaders);
    }


    public Object getAttribute(String paramName){
        return null;
    }

    public List<String> getParamNames(){
        return Collections.EMPTY_LIST;
    }

    public Object getParamValue(String paramName){
        return null;

    }

}
