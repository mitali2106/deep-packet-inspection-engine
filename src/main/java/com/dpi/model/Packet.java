package com.dpi.model;

public class Packet {

    public byte[] rawData;
    public int length;
    public long timestamp;

    public int srcIp;
    public int dstIp;
    public short srcPort;
    public short dstPort;
    public byte protocol;

    public byte[] payload;
    public int payloadLength;

    public FiveTuple toFiveTuple() {
        return FiveTuple.normalized(srcIp, dstIp, srcPort, dstPort, protocol);
    }
}