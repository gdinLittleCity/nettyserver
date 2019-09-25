package com.littlecity.server.entity;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.exception.HttpResponseHeaderException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
public class CustomHttpResponse extends DefaultFullHttpResponse {
    public CustomHttpResponse(HttpVersion version, HttpResponseStatus status) {
        super(version, status);
    }

    public CustomHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
        super(version, status, content);
    }

    public CustomHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) {
        super(version, status, content, validateHeaders);
    }


    public void addHeader(String headerName, String value) throws HttpResponseHeaderException {
        if (StringUtils.isEmpty(headerName)){
            throw new HttpResponseHeaderException("not set empty header name");
        }
        this.headers().add(headerName, value);
    }

    /**
     * 设置响应体,响应类型将会根据content-type的值进行格式化.默认格式化为JSON
     * @param data
     * @param charset
     */
    public void setContent(Object data, Charset charset){
        if (data == null){
            return;
        }
        if (charset == null){
            charset = Charset.forName(CharEncoding.UTF_8);
        }

        ByteBuf content = this.content();
        String contentType = getContentType();
        if (HttpResponseContentType.APPLICATION_JSON.equals(contentType)){
            String body = JSON.toJSONString(data);
            content.writeBytes(body.getBytes(charset));
            return;
        }

        content.writeBytes(data.toString().getBytes(charset));
        return;
    }

    private String getContentType(){
        return this.headers().get("content-type");
    }

}
