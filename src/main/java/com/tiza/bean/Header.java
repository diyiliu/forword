package com.tiza.bean;

import com.tiza.utils.Common;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Date;

/**
 * Description: Header
 * Author: DIYILIU
 * Update: 2015-09-22 9:22
 */
public class Header {

    private int length;
    private int forwardVersion;
    private int source;
    private Date datetime;
    private int messageType;
    private int cmd;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getForwardVersion() {
        return forwardVersion;
    }

    public void setForwardVersion(int forwardVersion) {
        this.forwardVersion = forwardVersion;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public byte[] toBytes() {
        ByteBuf buf = Unpooled.buffer(12);
        buf.writeShort(length);
        buf.writeByte(forwardVersion);
        buf.writeByte(source);
        buf.writeBytes(Common.dateToBytes(datetime));
        buf.writeByte(messageType);
        buf.writeByte(cmd);

        return buf.array();
    }
}
