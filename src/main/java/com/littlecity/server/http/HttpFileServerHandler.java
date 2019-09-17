package com.littlecity.server.http;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.config.ServerConfig;
import com.littlecity.server.entity.HttpRequestType;
import com.littlecity.server.entity.RespResult;
import com.littlecity.server.utils.HttpContextUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaocheng
 * @Date
 */
@Slf4j
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static ServerConfig CONFIG = ConfigFactory.create(ServerConfig.class);

    static {
        DiskFileUpload.baseDirectory = CONFIG.fileDir();

    }
    @Override
    protected void messageReceived(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            sendMessage(context, HttpResponseStatus.BAD_REQUEST, "bad request");
            return;
        }
        if (!request.getMethod().equals(HttpMethod.POST) && !request.getMethod().equals(HttpMethod.POST)){
            sendMessage(context, HttpResponseStatus.SERVICE_UNAVAILABLE, "not support this request method.");
            return;
        }

        String uri = request.getUri();
        log.info("request uri is :{}", uri);
        String contentType = HttpContextUtils.getContentType(request);

        Map<String, Object> requestParamMap = HttpContextUtils.getRequestParamMap(request);
        if (contentType.equals(HttpRequestType.MULTIPART_FORM_DATA)){
            FileUpload fileUpload = (FileUpload) requestParamMap.get("file");
            String module = (String) requestParamMap.get("module");
            StringBuilder sb = new StringBuilder();
            sb.append(DiskFileUpload.baseDirectory)
                    .append(File.separator)
                    .append(module)
                    .append(File.separator)
                    .append(fileUpload.getFilename());
            if (fileUpload.isCompleted()) {
                File newFile = new File(sb.toString());
                boolean renameResult = fileUpload.renameTo(newFile);
//                File file = fileUpload.getFile();
//                boolean renameResult = file.renameTo();

                log.info("rename to result:{}", renameResult);
            }

            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("url", sb.toString());
            sendMessage(context, HttpResponseStatus.OK, RespResult.of(resultMap));
            return;
        } else {

            log.info("param:{}", JSON.toJSONString(requestParamMap));
            sendMessage(context, HttpResponseStatus.OK, RespResult.ok());
            return;
        }

//        ByteBuf content = request.content();
//        byte[] contentData = new byte[content.readableBytes()];
//        content.readBytes(contentData);
//        System.out.println("content:" + new String(contentData, "UTF-8"));
    }



    private void sendMessage(ChannelHandlerContext context, HttpResponseStatus statusCode, Object msg) {

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, statusCode);
        ByteBuf content = response.content();
        content.writeBytes(JSON.toJSONString(msg).getBytes());

        response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());

        context.write(response);
        context.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
