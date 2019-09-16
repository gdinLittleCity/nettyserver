package com.littlecity.server.http;

import com.alibaba.fastjson.JSON;
import com.littlecity.server.entity.HttpRequestType;
import com.littlecity.server.entity.RespResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author huangxiaocheng
 * @Date
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void messageReceived(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            sendMessage(context, HttpResponseStatus.BAD_REQUEST, "bad request");
        }

        String uri = request.getUri();
        System.out.println("uri:" + uri);

        String contentType = request.headers().get(HttpHeaders.Names.CONTENT_TYPE);
        if (!StringUtils.isEmpty(contentType) && contentType.contains(HttpRequestType.MULTIPART_FORM_DATA)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
            for (InterfaceHttpData data : bodyHttpDatas) {

                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    String filename = fileUpload.getFilename();
                    System.out.println("fileName:" + filename);
                    if (fileUpload.isCompleted()) {
                        fileUpload.renameTo(new File(filename));
                    }

                }
            }

            sendMessage(context, HttpResponseStatus.OK, RespResult.ok());
            return;

        }


        ByteBuf content = request.content();
        byte[] contentData = new byte[content.readableBytes()];
        content.readBytes(contentData);
        System.out.println("content:" + new String(contentData, "UTF-8"));

        sendMessage(context, HttpResponseStatus.OK, RespResult.ok());
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
