package com.tiza.handler.gateway;

import com.tiza.bean.Header;
import com.tiza.cache.ICache;
import com.tiza.client.Forward;
import com.tiza.utils.Common;
import com.tiza.utils.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Description: GwHandler
 * Author: DIYILIU
 * Update: 2015-09-16 14:32
 */
public class GwHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ChannelHandlerContext context;

    private boolean writeable = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("建立连接...");
        this.context = ctx;

        // 发送登录指令
        sendLogin();

        ctx.channel().eventLoop().scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {

                if (writeable) {
                    Queue<byte[]> queue = Forward.getPool();

                    while (!queue.isEmpty()) {
                        byte[] bytes = queue.poll();

                        context.writeAndFlush(Unpooled.copiedBuffer(bytes));
                    }
                }
            }
        }, 1, 10, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //logger.info("收到消息...");

        ByteBuf buf = (ByteBuf) msg;

        // 读协议头
        buf.readBytes(new byte[11]);

        int cmd = buf.readByte();

        if (cmd == 0x12) {

            int result = buf.readByte();
            if (result == 1) {
                logger.info("登录网关成功...");

                writeable = true;
                Forward.doLogin(true);
            } else if (result == 0) {
                int len = buf.readUnsignedShort();

                byte[] bytes = new byte[len];

                logger.error("登录网关失败！[{}]", new String(bytes, "UTF-8"));

                // 发送登录指令
                sendLogin();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("通讯异常...{}", cause.getMessage());
        ctx.close();
        writeable = false;
        Forward.doLogin(false);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String key = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");

        // 心跳处理
        if (evt instanceof IdleStateEvent) {

            IdleStateEvent event = (IdleStateEvent) evt;

            if (IdleState.READER_IDLE == event.state()) {
                //logger.warn("读超时...[{}]", key);

            } else if (IdleState.WRITER_IDLE == event.state()) {
                //logger.warn("写超时...");
                // 协议头
                Header header = (Header) SpringUtil.getBean("header");
                header.setLength(12);
                header.setDatetime(new Date());
                header.setCmd(0x00);

                ctx.writeAndFlush(Unpooled.copiedBuffer(header.toBuffer()));
            } else if (IdleState.ALL_IDLE == event.state()) {
                //logger.warn("读/写超时...");
            }
        }
    }


    public void sendLogin() {

        // 协议头
        Header header = (Header) SpringUtil.getBean("header");
        header.setLength(12 + 4);
        header.setDatetime(new Date());
        header.setCmd(0x02);

        Long now = System.currentTimeMillis() / (1000 * 3600);
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(header.toBuffer());
        buf.writeInt(now.intValue());

        // 登陆指令
        context.writeAndFlush(buf);
    }
}
