package com.littlecity.server.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.littlecity.server.config.ServerConfig;
import com.littlecity.server.entity.HttpRequestType;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.*;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.codec.CharEncoding.UTF_8;

/**
 * @author huangxiaocheng
 * @Date 2019/9/17
 **/
@Slf4j
public class HttpContextUtils {

    private static ServerConfig CONFIG = ConfigFactory.create(ServerConfig.class);
    private static HttpDataFactory FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);


    public static Map<String, Object> getRequestParamMap(FullHttpRequest request) throws IOException {
        Map<String, Object> requestMap = new HashMap<>();

        Map<String, Object> parameters = getQueryStringParamMap(request.getUri());
        requestMap.putAll(parameters);

        // get请求参数集成
        if (request.getMethod().equals(HttpMethod.GET)){
            return requestMap;
        }
        // 非 get/post 请求,不做处理
        else if (!request.getMethod().equals(HttpMethod.POST)){
            return Collections.emptyMap();
        }


        String contentType = getContentType(request);
        if (StringUtils.isEmpty(contentType)) {
            log.error("after parse. content-type is null . not support.");
            return requestMap;
        }
        log.debug("after parse. content-type is:{}", contentType);
        // multipart/form-data
        if (contentType.equals(HttpRequestType.MULTIPART_FORM_DATA)) {
            commonPostParamGen(request, requestMap);

        }
        // application/json
        else if (HttpRequestType.APPLICATION_JSON.equals(contentType)) {

            String jsonParam = request.content().toString(Charsets.toCharset(UTF_8));
            JSONObject jsonObject = JSON.parseObject(jsonParam);
            jsonObject.entrySet().forEach(item -> requestMap.put(item.getKey(), item.getValue()));

        }
        // application/x-www-form-urlencoded
        else if (HttpRequestType.APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType)) {

            commonPostParamGen(request, requestMap);
        }


        return requestMap;
    }

    /**
     * 将uri中的参数与值转换为Map
     * @param uri
     * @return
     */
    public static Map<String, Object> getQueryStringParamMap(String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = decoder.parameters();

        Map<String, Object> routeResultMap = new HashMap<>(parameters.size());
        parameters.entrySet().forEach(parameterEntry -> {
            List<String> value = parameterEntry.getValue();
            if (value == null || value.size() <=0 ){
                return;
            }
            if (value.size() == 1){
                routeResultMap.put(parameterEntry.getKey(), value.get(0));
            } else {
                routeResultMap.put(parameterEntry.getKey(), value);
            }
        });
        return routeResultMap;
    }

    /**
     * post请求body内容的参数map化
     * @param request
     * @param requestMap
     * @throws IOException
     */
    private static void commonPostParamGen(FullHttpRequest request, Map<String, Object> requestMap) throws IOException {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(FACTORY, request, Charsets.toCharset(UTF_8));
        List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : bodyHttpDatas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attr = (Attribute) data;
                requestMap.put(attr.getName(), attr.getValue());
            }

            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                FileUpload fileUpload = (FileUpload) data;
                requestMap.put(fileUpload.getName(), fileUpload);
            }

        }
    }

    public static String getContentType(FullHttpRequest request) {

        String contentType = request.headers().get(HttpHeaders.Names.CONTENT_TYPE);
        log.info("content-type is:{}", contentType);

        String[] list = contentType.split(";");
        if (list != null && list.length > 0) {
            return list[0];
        }

        return null;
    }

    /**
     *  if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
     *                     FileUpload fileUpload = (FileUpload) data;
     *                     String filename = fileUpload.getFilename();
     *                     System.out.println("fileName:" + filename);
     *                     if (fileUpload.isCompleted()) {
     *                         fileUpload.renameTo(new File(CONFIG + filename));
     *                     }
     *
     *                 } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute){
     *                     Attribute attribute = (Attribute) data;
     *                     data.getName();
     *                     String value = attribute.getValue();
     *                 }
     */

}
