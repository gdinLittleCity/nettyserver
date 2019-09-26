package com.littlecity.server.netty;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter {

    private int counter = 1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client is active, send message");
        for (int i = 0; i < 100; i++) {


            UserInfo userInfo = new UserInfo();
            userInfo.setAge(i);
            userInfo.setName("name-"+i);
            ctx.write(userInfo);

        }
        ctx.flush();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{


        System.out.println("client recevie:"+ msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
