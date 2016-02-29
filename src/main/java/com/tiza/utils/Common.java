package com.tiza.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Calendar;
import java.util.Date;

/**
 * Description: Common
 * Author: DIYILIU
 * Update: 2015-09-17 9:15
 */
public class Common {


    public static boolean isEmpty(String str) {

        if (str == null || str.trim().length() < 1) {
            return true;
        }

        return false;
    }


    public static String bytesToIp(byte[] bytes) {

        if (bytes.length == 4) {

            StringBuilder builder = new StringBuilder();

            for (byte b : bytes) {

                builder.append((int) b & 0xff).append(".");
            }

            return builder.substring(0, builder.length() - 1);
        }

        return null;
    }

    public static byte[] dateToBytes(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return new byte[]{(byte) year, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second};
    }

    public static long bytesToLong(byte[] bytes) {

        long l = 0;
        for (int i = 0; i < bytes.length; i++) {
            l = l + (long) ((bytes[i] & 0xff) * Math.pow(256, bytes.length - i - 1));
        }
        return l;
    }


    public static byte[] longToBytes(long number) {

        long temp = number;

        byte[] bytes = new byte[5];

        for (int i = bytes.length - 1; i > -1; i--)
        {

            bytes[i] = new Long(temp & 0xff).byteValue();

            temp = temp >> 8;

        }

        return bytes;
    }

    public static String byteToString(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (byte a : bytes) {
            buf.append(String.format("%02X", getNoSin(a))).append(" ");
        }

        return buf.substring(0, buf.length() - 1);
    }

    public static int getNoSin(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return 256 + b;
        }
    }

    public static String parseBytes(byte[] array, int offset, int lenght){

        ByteBuf buf = Unpooled.copiedBuffer(array, offset, lenght);

        byte[] bytes = new  byte[buf.readableBytes()];
        buf.readBytes(bytes);

        return new String(bytes);
    }

    public static String parseVIN(byte[] array, int offset){

        ByteBuf buf = Unpooled.copiedBuffer(array);
        buf.readBytes(new byte[offset]);

        int len = buf.readByte();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);

        return new String(bytes);
    }

    public static byte[] restoreBinary(String content){

        String[] array = content.split(" ");

        byte[] bytes = new byte[array.length];

        for (int i = 0; i < array.length; i++){

            bytes[i] = Integer.valueOf(array[i], 16).byteValue();
        }

        return bytes;
    }


    public static void main(String[] args) {

        /*
        byte[] array = restoreBinary("00 56 01 05 10 01 05 0F 36 1F 01 04 " +
                "11 58 55 47 30 32 36 33 33 4A " +
                "45 4A 45 30 33 32 39 31 06 35 " +
                "5F 41 30 30 33 01 9B FC C0 06 " +
                "32 EA 00 00 98 00 00 0E 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 0F 10 01 05 0F 36 1A 10 02 " +
                "00 0C 00 FF 00 01 00 59 C2 CC " +
                "17 7C 6C 0A");

        System.out.println(parseVIN(array, 12));*/

    }
}
