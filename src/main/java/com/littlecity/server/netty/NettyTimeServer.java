package com.littlecity.server.netty;

import com.littlecity.server.netty.decode.MessagePackDecode;
import com.littlecity.server.netty.encode.MessagePackEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class NettyTimeServer {

    public void bind(int port){
        //初始化 主线程与工作线程
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        //服务端配置ServerBootStrap
        bootstrap.group(boosGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                // 请求队列最大长度
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 请求处理类配置
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0,2,0,2));
                        socketChannel.pipeline().addLast(new MessagePackDecode());

                        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
                        socketChannel.pipeline().addLast(new MessagePackEncode());

                        socketChannel.pipeline().addLast(new TimeServerHandler());
                    }
                });

        try {
            //绑定监听端口
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待端口关闭
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放线程资源
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }


    public static void main(String[] args) {
        new NettyTimeServer().bind(8082);
    }

}
