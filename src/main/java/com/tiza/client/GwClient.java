package com.tiza.client;

import com.tiza.handler.gateway.GwDecoder;
import com.tiza.handler.gateway.GwEncoder;
import com.tiza.handler.gateway.GwHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description: GwClient
 * Author: DIYILIU
 * Update: 2015-09-17 11:07
 */
public class GwClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ExecutorService executor = Executors.newScheduledThreadPool(1);

    private String tlbsHost;
    private int tlbsPort;

    public GwClient(String tlbsHost, int tlbsPort) {
        this.tlbsHost = tlbsHost;
        this.tlbsPort = tlbsPort;
    }

    public void connect(String host, int port) {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new GwDecoder())
                                    .addLast(new GwEncoder())
                                    .addLast(new IdleStateHandler(0, 30, 0))
                                    .addLast(new GwHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("连接异常！{}", e.getMessage());
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    logger.info("正在重连...");

                    try {
                        // 断开10秒后重连
                        TimeUnit.SECONDS.sleep(10);

                        TlbsClient.getInstance().connect(getTlbsHost(), getTlbsPort());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public String getTlbsHost() {
        return tlbsHost;
    }

    public int getTlbsPort() {
        return tlbsPort;
    }
}
