package com.tiza.client;

import com.tiza.bean.Header;
import com.tiza.bean.WorkParam;
import com.tiza.cache.ICache;
import com.tiza.utils.Common;
import com.tiza.utils.DateUtil;
import com.tiza.utils.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Description: Forward
 * Author: DIYILIU
 * Update: 2015-09-18 10:10
 */
public class Forward {

    private static Logger logger = LoggerFactory.getLogger(Forward.class);

    private static ConcurrentLinkedDeque<byte[]> pool = new ConcurrentLinkedDeque<byte[]>();

    private static boolean isConnected = false;

    public static void connect(String host, int port) {

        logger.info("DCS转发连接tlbs...");

        TlbsClient.getInstance().connect(host, port);
    }

    public static void setSource(int source){

        Header header = (Header) SpringUtil.getBean("header");
        header.setSource(source);
    }

    public static void doLogin(boolean flag) {

        isConnected = flag;
    }

    public static void put(byte[] bytes) {

        if (!isConnected) {
            logger.warn("尚未建立连接，发送数据失败...");

            return;
        }

        pool.add(bytes);
    }

    public static Queue getPool() {

        return pool;
    }

    public static void sendPosition(String vinCode, String softVersion, int lat, int lon,
                                    int speed, int direction, int height, byte[] state, String datetime) {

        ICache monitorCache = (ICache) SpringUtil.getBean("monitorCache");
        Header header = (Header) SpringUtil.getBean("header");

        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(vinCode.getBytes().length);
        buf.writeBytes(vinCode.getBytes());
        buf.writeByte(softVersion.getBytes().length);
        buf.writeBytes(softVersion.getBytes());
        buf.writeInt(lat);
        buf.writeInt(lon);
        buf.writeByte(speed);
        buf.writeByte(direction);
        buf.writeShort(height);
        buf.writeByte(state.length);
        buf.writeBytes(state);
        buf.writeBytes(Common.dateToBytes(DateUtil.stringToDate(datetime)));

        header.setLength(12 + buf.readableBytes());
        header.setDatetime(new Date());
        header.setCmd(0x03);

        ByteBuf byteBuf = Unpooled.copiedBuffer(header.toBuffer(), buf);
        byte[] bytes = byteBuf.array();

        put(bytes);

        if (monitorCache.containsKey(vinCode)){
            logger.error("发送数据到网关...[{},{},{}]", new Object[] { "位置", vinCode, Common.byteToString(bytes) });
        }
    }


    public static void sendWorkData(String vinCode, String softVersion, int lat, int lon,
                                    int speed, int direction, int height, byte[] state, String datetime,
                                    int type, int length, List<WorkParam> workParams) {

        ICache monitorCache = (ICache) SpringUtil.getBean("monitorCache");
        Header header = (Header) SpringUtil.getBean("header");

        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(vinCode.getBytes().length);
        buf.writeBytes(vinCode.getBytes());
        buf.writeByte(softVersion.getBytes().length);
        buf.writeBytes(softVersion.getBytes());
        buf.writeInt(lat);
        buf.writeInt(lon);
        buf.writeByte(speed);
        buf.writeByte(direction);
        buf.writeShort(height);
        buf.writeByte(state.length);
        buf.writeBytes(state);
        buf.writeBytes(Common.dateToBytes(DateUtil.stringToDate(datetime)));

        buf.writeShort(type);
        buf.writeShort(length);
        for (WorkParam workParam : workParams) {
            buf.writeInt(workParam.getCanId());
            buf.writeBytes(workParam.getContent());
        }

        header.setLength(12 + buf.readableBytes());
        header.setDatetime(new Date());
        header.setCmd(0x04);

        ByteBuf byteBuf = Unpooled.copiedBuffer(header.toBuffer(), buf);
        byte[] bytes = byteBuf.array();

        put(bytes);

        if (monitorCache.containsKey(vinCode)){
            logger.error("发送数据到网关...[{},{},{}]", new Object[] { "工况", vinCode, Common.byteToString(bytes) });
        }
    }
}
