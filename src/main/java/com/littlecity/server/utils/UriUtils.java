package com.littlecity.server.utils;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author huangxiaocheng
 * @Date 2019/9/20
 **/
public class UriUtils {


    public static void main(String[] args) {
//        BImp b = new BImp();
//
//        boolean bins = b instanceof InterfaceA;
//
//        System.out.println("b instanceof InterfaceA :"+bins);

        DefaultHttpRequest defaultHttpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "", true);

        boolean jk = defaultHttpRequest instanceof FullHttpRequest;

        System.out.println(" defaultHttpRequest instanceof FullHttpRequest:"+ jk);
    }
}
