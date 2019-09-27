package com.littlecity.server.http;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.config.ServerConfig;
import com.littlecity.server.entity.CustomHttpRequest;
import com.littlecity.server.entity.CustomHttpResponse;
import com.littlecity.server.http.controller.HttpRequestController;
import com.littlecity.server.router.http.Router;
import com.littlecity.server.router.http.RouterResult;
import com.littlecity.server.utils.HttpContextUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
        long currentTime = System.currentTimeMillis();
        if (!request.getDecoderResult().isSuccess()) {
            sendMessage(context, HttpResponseStatus.BAD_REQUEST, "bad request");
            return;
        }
        if (!request.getMethod().equals(HttpMethod.POST) && !request.getMethod().equals(HttpMethod.GET)){
            sendMessage(context, HttpResponseStatus.SERVICE_UNAVAILABLE, "not support this request method.");
            return;
        }
        InetSocketAddress socketAddress = (InetSocketAddress) context.channel().localAddress();


        String uri = request.getUri();
        log.debug("request uri is :{}", uri);

        log.debug("router begin.");

        RouterResult route = router.route(request.getMethod(), uri);
        long routerTime = System.currentTimeMillis() - currentTime;
        log.info("router end. result:{},program total cost:{} ms", route, routerTime);

        Class controllerClazz = route.getController();
        HttpRequestController controller = (HttpRequestController) controllerClazz.newInstance();


        CustomHttpRequest httpRequest = new CustomHttpRequest(HttpVersion.HTTP_1_1, request.getMethod(), request.getUri(), request.content());
        httpRequest.headers().set(request.headers());

        Map<String, Object> requestParamMap = HttpContextUtils.getRequestParamMap(request);
        httpRequest.setParameterMap(requestParamMap);

        httpRequest.setLocalAddress(socketAddress);

        CustomHttpResponse httpResponse = new CustomHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        controller.doService(httpRequest, httpResponse);
        long controllerTime =  System.currentTimeMillis() - currentTime;
        log.info("controller end. program total cost:{} ms", controllerTime);

        context.write(httpResponse);
        context.flush();
        context.close();
        log.info(" data write over. program total cost:{} ms", System.currentTimeMillis() - currentTime);

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
