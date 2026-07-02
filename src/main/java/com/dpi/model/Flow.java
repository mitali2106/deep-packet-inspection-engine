package com.dpi.model;

import java.util.concurrent.atomic.AtomicLong;

public class Flow {

    public AppType appType = AppType.UNKNOWN;
    public String sni = null;
    public boolean blocked = false;
    public AtomicLong packetCount = new AtomicLong(0);
    public long firstSeen = System.currentTimeMillis();

    @Override
    public String toString() {
        return "Flow{" +
               "appType=" + appType +
               ", sni='" + sni + '\'' +
               ", blocked=" + blocked +
               ", packets=" + packetCount.get() +
               '}';
    }
}