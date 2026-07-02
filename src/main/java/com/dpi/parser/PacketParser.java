package com.dpi.parser;

import com.dpi.model.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketParser {

    private static final int ETHERTYPE_IPV4 = 0x0800;
    private static final int ETHERTYPE_IPV6 = 0x86DD;
    private static final byte PROTO_TCP = 6;

    public static boolean parse(Packet packet) {
        byte[] data = packet.rawData;
        if (data.length < 14) return false;

        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.BIG_ENDIAN);

        int etherType = ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);

        if (etherType == ETHERTYPE_IPV4) {
            return parseIPv4(packet, data, buf);
        } else if (etherType == ETHERTYPE_IPV6) {
            return parseIPv6(packet, data, buf);
        }

        return false;
    }

    private static boolean parseIPv4(Packet packet, byte[] data, ByteBuffer buf) {
        if (data.length < 34) return false;

        int ipHeaderLen = (data[14] & 0x0F) * 4;
        packet.protocol = data[14 + 9];

        if (packet.protocol != PROTO_TCP) return false;

        packet.srcIp = buf.getInt(14 + 12);
        packet.dstIp = buf.getInt(14 + 16);

        int tcpStart = 14 + ipHeaderLen;
        if (data.length < tcpStart + 20) return false;

        packet.srcPort = buf.getShort(tcpStart);
        packet.dstPort = buf.getShort(tcpStart + 2);

        int tcpHeaderLen = ((data[tcpStart + 12] & 0xFF) >> 4) * 4;
        int payloadStart = tcpStart + tcpHeaderLen;

        extractPayload(packet, data, payloadStart);
        return true;
    }

    private static boolean parseIPv6(Packet packet, byte[] data, ByteBuffer buf) {
        if (data.length < 54) return false;

        byte nextHeader = data[14 + 6];
        if (nextHeader != PROTO_TCP) return false;

        packet.protocol = nextHeader;

        packet.srcIp = buf.getInt(14 + 8);
        packet.dstIp = buf.getInt(14 + 24);

        int tcpStart = 14 + 40;
        if (data.length < tcpStart + 20) return false;

        packet.srcPort = buf.getShort(tcpStart);
        packet.dstPort = buf.getShort(tcpStart + 2);

        int tcpHeaderLen = ((data[tcpStart + 12] & 0xFF) >> 4) * 4;
        int payloadStart = tcpStart + tcpHeaderLen;

        extractPayload(packet, data, payloadStart);
        return true;
    }

    private static void extractPayload(Packet packet, byte[] data, int payloadStart) {
        if (payloadStart >= data.length) {
            packet.payload = new byte[0];
            packet.payloadLength = 0;
        } else {
            packet.payloadLength = data.length - payloadStart;
            packet.payload = new byte[packet.payloadLength];
            System.arraycopy(data, payloadStart, packet.payload, 0, packet.payloadLength);
        }
    }
}