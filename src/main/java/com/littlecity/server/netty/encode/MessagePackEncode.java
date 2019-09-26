package com.littlecity.server.netty.encode;

import com.littlecity.server.netty.UserInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * 消息包编码器
 * @author huangxiaocheng
 */
public class MessagePackEncode extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack mp = new MessagePack();
        mp.register(UserInfo.class);
        try {
            byte[] out = mp.write(o);
            byteBuf.writeBytes(out);

        } catch (Exception ex){
            ex.printStackTrace();
        }


    }
}
