package com.tiza.handler.tlbs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description: TlbsEncoder
 * Author: DIYILIU
 * Update: 2015-09-16 14:58
 */
public class TlbsEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        if (msg != null) {

            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            out.writeBytes(bytes);
        }
    }
}
