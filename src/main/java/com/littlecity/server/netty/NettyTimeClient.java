package com.littlecity.server.netty;

import com.littlecity.server.netty.decode.MessagePackDecode;
import com.littlecity.server.netty.encode.MessagePackEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


public class NettyTimeClient {

    public void connect(String host, int port){
        EventLoopGroup work = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0,2,0,2));
                        socketChannel.pipeline().addLast(new MessagePackDecode());

                        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
                        socketChannel.pipeline().addLast(new MessagePackEncode());

                        socketChannel.pipeline().addLast(new TimeClientHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
        }
    }

   /* public static void main(String[] args) {
        new NettyTimeClient().connect("127.0.0.1", 8082);
    }*/

}
