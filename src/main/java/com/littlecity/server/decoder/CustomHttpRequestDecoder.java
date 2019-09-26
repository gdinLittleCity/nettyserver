package com.littlecity.server.decoder;

import com.littlecity.server.entity.CustomHttpRequest;
import io.netty.handler.codec.http.*;

/**
 * @author huangxiaocheng
 * @Date 2019/9/25
 **/
public class CustomHttpRequestDecoder {

 /*   public CustomHttpRequestDecoder() {
    }

    public CustomHttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true);
    }

    public CustomHttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) throws Exception {

        return new CustomHttpRequest(HttpVersion.valueOf(initialLine[2]), HttpMethod.valueOf(initialLine[0]), initialLine[1], this.validateHeaders);
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new CustomHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/bad-request", this.validateHeaders);
    }

    @Override
    protected boolean isDecodingRequest() {
        return true;
    }*/
}
