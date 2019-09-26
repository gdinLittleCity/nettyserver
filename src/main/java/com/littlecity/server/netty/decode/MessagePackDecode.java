package com.littlecity.server.netty.decode;

import com.littlecity.server.netty.UserInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;

import java.util.List;

/**
 * 消息包解码器
 * @author huangxiaocheng
 */
public class MessagePackDecode extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        MessagePack mp = new MessagePack();
        mp.register(UserInfo.class);
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes, 0, bytes.length);

        Value input = mp.read(bytes);

        list.add(input);


    }
}
