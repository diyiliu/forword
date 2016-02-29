package com.tiza.handler.tlbs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description: TlbsDecoder
 * Author: DIYILIU
 * Update: 2015-09-16 15:05
 */
public class TlbsDecoder extends ByteToMessageDecoder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < 12) {

            return;
        }

        in.markReaderIndex();

        // 读协议头
        in.readBytes(new byte[12]);

        int result = in.readByte();

        // 登录失败
        if (result == 0) {

            int len = in.readInt();

            if (in.readableBytes() < len){

                in.resetReaderIndex();

                return;
            }


            in.resetReaderIndex();
            byte[] bytes = new byte[12 + 1 + 4 + len];
            in.readBytes(bytes);

            out.add(Unpooled.copiedBuffer(bytes));
            // 登录成功
        } else if (result == 1) {

            if (in.readableBytes() < 6){

                in.resetReaderIndex();

                return;
            }

            in.resetReaderIndex();
            byte[] bytes = new byte[12 + 1 + 6];
            in.readBytes(bytes);

            out.add(Unpooled.copiedBuffer(bytes));
        }else{
            logger.error("结果异常！[result={}]", result);
            ctx.close();
        }

    }
}
