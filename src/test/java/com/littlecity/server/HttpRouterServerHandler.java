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

import com.littlecity.server.router.RouteResult;
import com.littlecity.server.router.Router;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class HttpRouterServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    // For simplicity of this example, route targets are just simple strings.
    // But you can make them classes, and here once you get a target class,
    // you can create an instance of it and dispatch the request to the instance etc.
    private final Router<String> router;

    HttpRouterServerHandler(Router<String> router) {
        this.router = router;
    }


    // Display debug info.
    private static HttpResponse createResponse(HttpRequest req, Router<String> router) {
        RouteResult<String> routeResult = router.route(req.getMethod(), req.getUri());

        String content =
                "router: \n" + router + "\n" +
                "req: " + req + "\n\n" +
                "routeResult: \n" +
                "target: " + routeResult.target() + "\n" +
                "pathParams: " + routeResult.pathParams() + "\n" +
                "queryParams: " + routeResult.queryParams() + "\n\n" +
                "allowedMethods: " + router.allowedMethods(req.getUri());

        FullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)
        );

        res.headers().set(HttpHeaders.Names.CONTENT_TYPE,   "text/plain");
        res.headers().set(HttpHeaders.Names.CONTENT_LENGTH, res.content().readableBytes());

        return res;
    }

    private static void flushResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {


            res.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            ctx.writeAndFlush(res);

    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        HttpResponse res = createResponse(req, router);
        flushResponse(ctx, req, res);
    }
}
