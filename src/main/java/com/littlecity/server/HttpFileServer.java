package com.littlecity.server;

import com.littlecity.server.config.ServerConfig;
import com.littlecity.server.http.HttpFileServerHandler;
import com.littlecity.server.router.http.Router;
import com.littlecity.server.service.HelloController;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;


@Slf4j
public class HttpFileServer {

    private static ServerConfig cfg = ConfigFactory.create(ServerConfig.class);


    public void bind(int port){
        log.info("max file size:{}M", cfg.maxFileSize());
        Router router = new Router();
        // 配置router
        router.get("/getName", HelloController.class)
              .post("/getName", HelloController.class);

        router.logRouterList();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 请求数据 解码
                        socketChannel.pipeline().addLast("http-request-decoder", new HttpRequestDecoder());
                        // body数据合并
                        socketChannel.pipeline().addLast("http-aggregator",new HttpObjectAggregator( cfg.maxFileSize() * 1024 * 1024));
                        // 响应数据 编码
                        socketChannel.pipeline().addLast("http-response-encoder", new HttpResponseEncoder());
                        // 块处理
                        socketChannel.pipeline().addLast("http-chunk", new ChunkedWriteHandler());
                        socketChannel.pipeline().addLast("file-server-controller", new HttpFileServerHandler(router));

                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        log.info("http server start . listen port:{}", cfg.port());
        new HttpFileServer().bind(cfg.port());

    }
}
