package com.tiza.client;

import com.tiza.handler.tlbs.TlbsDecoder;
import com.tiza.handler.tlbs.TlbsEncoder;
import com.tiza.handler.tlbs.TlbsHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: TlbsClient
 * Author: DIYILIU
 * Update: 2015-09-16 14:27
 */
public class TlbsClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private TlbsClient() {
    }

    private static class InstanceHolder {

        public static final TlbsClient instance = new TlbsClient();
    }

    public static TlbsClient getInstance() {

        return InstanceHolder.instance;
    }

    public void connect(final String host, final int port) {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TlbsDecoder())
                                    .addLast(new TlbsEncoder())
                                    .addLast(new TlbsHandler(host, port));
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("连接异常！{}", e.getMessage());
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}