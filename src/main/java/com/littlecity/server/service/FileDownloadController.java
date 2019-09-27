package com.littlecity.server.service;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.config.ServerConfig;
import com.littlecity.server.entity.CustomHttpRequest;
import com.littlecity.server.entity.CustomHttpResponse;
import com.littlecity.server.entity.FileDisplay;
import com.littlecity.server.http.controller.AbstractHttpRequestController;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date 2019/9/27
 **/
@Slf4j
public class FileDownloadController extends AbstractHttpRequestController {
    private static ServerConfig CONFIG = ConfigFactory.create(ServerConfig.class);
    @Override
    public void doGet(CustomHttpRequest request, CustomHttpResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(CustomHttpRequest request, CustomHttpResponse response) throws IOException {
        Map<String, Object> parameterMap = request.getParameterMap();
        String module = (String) parameterMap.get("module");
        String fileName = (String) parameterMap.get("fileName");
        if (StringUtils.isEmpty(module) || StringUtils.isEmpty(fileName) || StringUtils.isEmpty((String) parameterMap.get("display"))){
            log.info("get resource param:{}, param error. module or fileName, display is empty", JSON.toJSONString(parameterMap));

            return ;
        }
        Integer display = Integer.valueOf((String) parameterMap.get("display"));



        String fileDir = CONFIG.fileDir();
        FileInputStream fis = null;
        try{
            File resoure = new File(fileDir + File.separator + module + File.separator + fileName);
            fis = new FileInputStream(resoure);
            byte[] fileByte = new byte[1024];
            while (fis.read(fileByte) > 0){
                response.content().writeBytes(fileByte);
            }
            String contentType = Files.probeContentType(Paths.get(resoure.toURI()));

            switch (display) {
                case FileDisplay.WEB:
                    response.addHeader("content-type",contentType);
                    break;
                case FileDisplay.DOWN_LOAD:
                    response.addHeader("content-type","application/octet-stream; charset=utf-8");
                    response.addHeader("Content-Disposition","attachment;filename=\""+ new String(fileName.getBytes(), "UTF-8") +"\"");
                    break;
                default:
                    break;

            }
        } finally {
            if (fis != null){
                fis.close();
            }
        }




    }
}
