package com.littlecity.server.http;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.config.ServerConfig;
import com.littlecity.server.entity.HttpRequestType;
import com.littlecity.server.entity.RespResult;
import com.littlecity.server.http.controller.HttpRequestController;
import com.littlecity.server.router.http.Router;
import com.littlecity.server.router.http.RouterResult;
import com.littlecity.server.service.HelloController;
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
    private Router router;

    private static ServerConfig CONFIG = ConfigFactory.create(ServerConfig.class);

    static {
        DiskFileUpload.baseDirectory = CONFIG.fileDir();

    }

    public HttpFileServerHandler(Router router){
        this.router = router;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            sendMessage(context, HttpResponseStatus.BAD_REQUEST, "bad request");
            return;
        }
        if (!request.getMethod().equals(HttpMethod.POST) && !request.getMethod().equals(HttpMethod.GET)){
            sendMessage(context, HttpResponseStatus.SERVICE_UNAVAILABLE, "not support this request method.");
            return;
        }

        String uri = request.getUri();
        log.info("request uri is :{}", uri);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);


        log.info("router begin.");
        RouterResult route = router.route(request.getMethod(), uri);
        log.info("router end. result:{}", route);

        Class controllerClazz = route.getController();
        HttpRequestController controller = (HttpRequestController) controllerClazz.newInstance();

        controller.doService(request, response);
        log.info("controller handler end");
        response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        context.writeAndFlush(response);

    }


    private void uploadFile(ChannelHandlerContext context, FullHttpRequest request) throws IOException {
        String contentType = HttpContextUtils.getContentType(request);

        Map<String, Object> requestParamMap = HttpContextUtils.getRequestParamMap(request);
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
            sendMessage(context, HttpResponseStatus.OK, RespResult.of(resultMap));
            return;
        } else {

            log.info("param:{}", JSON.toJSONString(requestParamMap));
            sendMessage(context, HttpResponseStatus.OK, RespResult.ok());
            return;
        }
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
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);

        ByteBuf content = response.content();
        content.writeBytes("system internal error.".getBytes());

        ctx.writeAndFlush(response);
        ctx.close();
        cause.printStackTrace();
    }
}
