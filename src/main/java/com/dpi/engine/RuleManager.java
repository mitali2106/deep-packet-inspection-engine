package com.dpi.engine;

import com.dpi.model.AppType;

import java.util.HashSet;
import java.util.Set;

public class RuleManager {

    private final Set<Integer> blockedIps = new HashSet<>();
    private final Set<AppType> blockedApps = new HashSet<>();
    private final Set<String> blockedDomains = new HashSet<>();

    public void blockIp(String ip) {
        blockedIps.add(ipToInt(ip));
    }

    public void blockApp(AppType app) {
        blockedApps.add(app);
    }

    public void blockDomain(String domain) {
        blockedDomains.add(domain.toLowerCase());
    }

    public boolean isBlocked(int srcIp, AppType appType, String sni) {
        if (blockedIps.contains(srcIp)) return true;
        if (appType != null && blockedApps.contains(appType)) return true;
        if (sni != null) {
            for (String domain : blockedDomains) {
                if (sni.toLowerCase().contains(domain)) return true;
            }
        }
        return false;
    }

    private int ipToInt(String ip) {
        String[] parts = ip.split("\\.");
        int result = 0;
        for (String part : parts) {
            result = (result << 8) | Integer.parseInt(part);
        }
        return result;
    }
}