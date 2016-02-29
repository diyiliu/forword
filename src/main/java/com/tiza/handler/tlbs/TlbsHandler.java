package com.tiza.handler.tlbs;

import com.tiza.bean.Header;
import com.tiza.client.GwClient;
import com.tiza.client.TlbsClient;
import com.tiza.utils.Common;
import com.tiza.utils.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Description: TlbsHandler
 * Author: DIYILIU
 * Update: 2015-09-16 14:32
 */
public class TlbsHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String tlbsHost;
    private int tlbsPort;

    public TlbsHandler(String tlbsHost, int tlbsPort) {
        this.tlbsHost = tlbsHost;
        this.tlbsPort = tlbsPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("建立连接...");

        // 协议头
        Header header = (Header) SpringUtil.getBean("header");
        header.setLength(12 + 6);
        header.setDatetime(new Date());
        header.setCmd(0x01);

        ByteBuf buf = Unpooled.buffer(12 + 6);

        Long hour = System.currentTimeMillis() / (1000 * 3600);

        buf.writeBytes(header.toBuffer());
        buf.writeInt(hour.intValue());
        buf.writeShort(2000);

        logger.info("发送登录指令...");
        ctx.writeAndFlush(Unpooled.copiedBuffer(buf));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //logger.info("收到消息...");

        ByteBuf buf = (ByteBuf) msg;

        // 读协议头
        buf.readBytes(new byte[11]);

        int cmd = buf.readByte();
        int result = buf.readByte();

        if (cmd != 0x11) {

            logger.error("指令异常！[{}]", cmd);
            ctx.close();
        }

        if (result == 0) {

            int len = buf.readInt();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);

            logger.error("登录失败！[{}]", new String(bytes));
            ctx.close();

            reconnect();
        }else if (result == 1) {
            byte[] ipArray = new byte[4];
            buf.readBytes(ipArray);

            String host = Common.bytesToIp(ipArray);
            int port = buf.readUnsignedShort();

            logger.info("ip: {}, port: {}", host, port);
            ctx.close();

            new GwClient(tlbsHost, tlbsPort).connect(host, port);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("通讯异常...{}", cause.getMessage());
        ctx.close();
    }


    public void reconnect() {

        logger.info("10秒后准备重连...");
        try {
            TimeUnit.SECONDS.sleep(10);

            TlbsClient.getInstance().connect(tlbsHost, tlbsPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}