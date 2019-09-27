package com.littlecity.server.service;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.entity.*;
import com.littlecity.server.http.controller.AbstractHttpRequestController;
import com.littlecity.server.utils.HttpContextUtils;
import com.littlecity.server.utils.RamdomUtils;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        String contentType = HttpContextUtils.getContentType(request);
        response.addHeader("content-type",HttpResponseContentType.APPLICATION_JSON);
        Map<String, Object> requestParamMap = request.getParameterMap();

        if (contentType.contains(HttpRequestType.MULTIPART_FORM_DATA)){
            List<FileUpload> fileUploadList = (List<FileUpload>) requestParamMap.get("file");
            String module = (String) requestParamMap.get("module");
            // 展示方式 1: 网页显示, 2: 下载
            String displayStr = (String) requestParamMap.get("display");
            Map<String, Object> resultMap = new HashMap<>();
            if(StringUtils.isEmpty(module) || StringUtils.isEmpty(displayStr)){
                response.setContent(RespResult.of("param : module or display is empty"), Charset.forName(CharEncoding.UTF_8));
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(DiskFileUpload.baseDirectory).append(File.separator).append(module);
            List<String> urls = new ArrayList<>();
            // 文件的磁盘持久化
            for (FileUpload fileUpload : fileUploadList) {
                String fileName = buildFileName(fileUpload, module);
                if (fileUpload.isCompleted()) {
                    File newFileParent = new File(sb.toString());
                    if (!newFileParent.exists()){
                        newFileParent.mkdirs();
                    }
                    File newFile = new File(newFileParent, fileName);
                    boolean renameResult = fileUpload.renameTo(newFile);
                    log.info("rename to result:{}", renameResult);
                }

                InetSocketAddress localAddress = request.getLocalAddress();
                String fileUrl = buildResourcePath(localAddress, module, Integer.valueOf(displayStr), fileName);
                urls.add(fileUrl);
            }

            resultMap.put("urls", urls);
            response.setContent(RespResult.of(resultMap), Charset.forName(CharEncoding.UTF_8));
            return;
        } else {

            response.setContent( RespResult.ok(), Charset.forName(CharEncoding.UTF_8));
            return;

        }

    }

    private String buildFileName(FileUpload fileUpload, String module) {
        StringBuilder sb = new StringBuilder();
        String uploadFilename = fileUpload.getFilename();
        sb.append(module)
                .append(System.currentTimeMillis())
                .append(RamdomUtils.getRamdom(5))
                .append(uploadFilename.substring(uploadFilename.lastIndexOf(".")));
        return sb.toString();
    }


    private String buildResourcePath(InetSocketAddress localAddress, String module, int display, String fileName){
        StringBuilder sb = new StringBuilder();
        sb.append("http://")
                .append( localAddress.getHostString())
                .append(":").append(localAddress.getPort())
                .append("/static").append("?")
                .append("module=").append(module).append("&")
                .append("display=").append(display).append("&")
                .append("fileName=").append(fileName);
        return sb.toString();
    }

}
