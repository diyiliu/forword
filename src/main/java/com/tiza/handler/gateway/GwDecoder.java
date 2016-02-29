package com.tiza.handler.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description: GwDecoder
 * Author: DIYILIU
 * Update: 2015-09-17 11:25
 */
public class GwDecoder extends ByteToMessageDecoder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < 12) {

            return;
        }

        in.markReaderIndex();
        // 读协议头
        in.readBytes(new byte[11]);

        int cmd = in.readByte();

        if (cmd == 0x10) {

            // 心跳内容为空，不作处理
        } else if (cmd == 0x12) {
            if (in.readableBytes() < 3) {
                in.resetReaderIndex();

                return;
            }

            int result = in.readByte();

            int len = in.readUnsignedShort();
            if (in.readableBytes() < len) {
                in.resetReaderIndex();

                return;
            }

            in.resetReaderIndex();
            byte[] bytes = new byte[12 + 3 + len];
            in.readBytes(bytes);

            out.add(Unpooled.copiedBuffer(bytes));
        } else {
            logger.error("指令异常！[cmd={}]", cmd);
            ctx.close();
        }
    }
}
