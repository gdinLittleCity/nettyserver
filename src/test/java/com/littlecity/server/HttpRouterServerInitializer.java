/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.littlecity.server;

import com.littlecity.server.router.BadClientSilencer;
import com.littlecity.server.router.Router;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpRouterServerInitializer extends ChannelInitializer<SocketChannel> {
    private final HttpRouterServerHandler handler;
    private final BadClientSilencer badClientSilencer = new BadClientSilencer();

    HttpRouterServerInitializer(Router<Class> router) {
        handler = new HttpRouterServerHandler(router);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline()
        // 请求数据 解码
        .addLast("http-request-decoder", new HttpRequestDecoder())
        // body数据合并
        .addLast("http-aggregator",new HttpObjectAggregator( 10 * 1024 * 1024))
        // 响应数据 编码
        .addLast("http-response-encoder", new HttpResponseEncoder())
        // 块处理
       .addLast("http-chunk", new ChunkedWriteHandler())
          .addLast(handler)
          .addLast(badClientSilencer);
    }
}
