package com.littlecity.server.service;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.entity.CustomHttpRequest;
import com.littlecity.server.entity.HttpRequestType;
import com.littlecity.server.entity.CustomHttpResponse;
import com.littlecity.server.entity.RespResult;
import com.littlecity.server.http.controller.AbstractHttpRequestController;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date 2019/9/23
 **/
@Slf4j
public class FileUploadController extends AbstractHttpRequestController {
    @Override
    public void doGet(CustomHttpRequest request, CustomHttpResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(CustomHttpRequest request, CustomHttpResponse response) throws IOException {
        String contentType = request.getHeader("Content-Type");
        Map<String, Object> requestParamMap = request.getParameterMap();

        if (contentType.equals(HttpRequestType.MULTIPART_FORM_DATA)){
            FileUpload fileUpload = (FileUpload) requestParamMap.get("file");
            String module = (String) requestParamMap.get("module");
            StringBuilder sb = new StringBuilder();
            sb.append(DiskFileUpload.baseDirectory)
                    .append(File.separator)
                    .append(module);
            if (fileUpload.isCompleted()) {
                File newFileParent = new File(sb.toString());
                if (!newFileParent.exists()){
                    newFileParent.mkdirs();
                }
                File newFile = new File(newFileParent, fileUpload.getFilename());
                boolean renameResult = fileUpload.renameTo(newFile);

                log.info("rename to result:{}", renameResult);
            }

            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("url", sb.toString());
            response.setContent(RespResult.of(resultMap), Charset.forName(CharEncoding.UTF_8));
            return;
        } else {

            log.info("param:{}", JSON.toJSONString(requestParamMap));
            response.setContent(RespResult.ok(), Charset.forName(CharEncoding.UTF_8));
            return;

        }

    }

}
