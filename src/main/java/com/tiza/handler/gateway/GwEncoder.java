package com.tiza.handler.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description: GwEncoder
 * Author: DIYILIU
 * Update: 2015-09-17 11:23
 */
public class GwEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        if (msg != null){
            ByteBuf buf = (ByteBuf) msg;

            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            out.writeBytes(bytes);
        }
    }
}
