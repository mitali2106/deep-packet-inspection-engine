package com.dpi.report;

import com.dpi.model.AppType;
import com.dpi.model.Flow;
import com.dpi.model.FiveTuple;

import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    public static void print(Map<FiveTuple, Flow> flowTable,
                             long totalPackets,
                             long forwarded,
                             long dropped) {

        System.out.println("\n==========================================");
        System.out.println("         DPI ENGINE - TRAFFIC REPORT     ");
        System.out.println("==========================================");
        System.out.printf("  Total Packets  : %d%n", totalPackets);
        System.out.printf("  Forwarded      : %d%n", forwarded);
        System.out.printf("  Dropped        : %d%n", dropped);
        System.out.printf("  Unique Flows   : %d%n", flowTable.size());
        System.out.println("==========================================");
        System.out.println("         APPLICATION BREAKDOWN           ");
        System.out.println("==========================================");

        Map<AppType, Long> appCounts = new HashMap<>();
        for (Flow flow : flowTable.values()) {
            appCounts.merge(flow.appType, flow.packetCount.get(), Long::sum);
        }

        appCounts.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .forEach(e -> {
                boolean isBlocked = flowTable.values().stream()
                    .anyMatch(f -> f.appType == e.getKey() && f.blocked);
                System.out.printf("  %-15s : %d packets%s%n",
                    e.getKey(), e.getValue(), isBlocked ? " (BLOCKED)" : "");
            });

        System.out.println("==========================================");
        System.out.println("         DETECTED DOMAINS                ");
        System.out.println("==========================================");

        flowTable.values().stream()
            .filter(f -> f.sni != null)
            .forEach(f -> System.out.printf("  %-30s -> %s%s%n",
                f.sni, f.appType, f.blocked ? " [BLOCKED]" : ""));

        System.out.println("==========================================");
    }
}