package com.littlecity.server.entity;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huangxiaocheng
 * @Date 2019/9/19
 **/
@Data
public class CustomHttpRequest extends DefaultHttpRequest {

    private Map<String,Object> parameterMap;

    public CustomHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        super(httpVersion, method, uri);
    }

    public CustomHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
        super(httpVersion, method, uri, validateHeaders);
    }


    public List<String> getParamNames(){
        return parameterMap.keySet().stream().collect(Collectors.toList());
    }

    public Object getParamValue(String paramName){
        return parameterMap.get(paramName);

    }


    public String getHeader(String name){
        if (StringUtils.isEmpty(name)){
            return null;
        }

        return this.headers().get(name);
    }

}
