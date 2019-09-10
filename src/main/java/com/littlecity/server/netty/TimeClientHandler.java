package com.littlecity.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter {

    private int counter = 1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 100; i++) {

            byte[] order = ("query time order" + System.getProperty("line.separator")).getBytes();
            ByteBuf message = Unpooled.buffer(order.length);
            message.writeBytes(order);
            ctx.writeAndFlush(message);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] resp = new byte[buf.readableBytes()];
//        buf.readBytes(resp);
//        String body = new String(resp, "UTF-8");
        String body = (String) msg;
        System.out.println("time is: " + body +",counter:"+ counter++);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
