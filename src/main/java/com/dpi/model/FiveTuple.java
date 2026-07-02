package com.dpi.model;

public record FiveTuple(
    int srcIp,
    int dstIp,
    short srcPort,
    short dstPort,
    byte protocol
) {
    public static FiveTuple normalized(int ip1, int ip2,
                                       short port1, short port2,
                                       byte protocol) {
        if (Integer.compareUnsigned(ip1, ip2) <= 0) {
            return new FiveTuple(ip1, ip2, port1, port2, protocol);
        } else {
            return new FiveTuple(ip2, ip1, port2, port1, protocol);
        }
    }

    public String srcIpString() {
        return ipToString(srcIp);
    }

    public String dstIpString() {
        return ipToString(dstIp);
    }

    private static String ipToString(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8)  & 0xFF) + "." +
               ( ip        & 0xFF);
    }

    @Override
    public String toString() {
        return srcIpString() + ":" + (srcPort & 0xFFFF) +
               " -> " +
               dstIpString() + ":" + (dstPort & 0xFFFF) +
               " [" + (protocol & 0xFF) + "]";
    }
}