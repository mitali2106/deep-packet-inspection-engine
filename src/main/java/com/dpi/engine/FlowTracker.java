package com.dpi.engine;

import com.dpi.model.AppType;
import com.dpi.model.Flow;
import com.dpi.model.FiveTuple;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class FlowTracker {

    private final ConcurrentHashMap<FiveTuple, Flow> flowTable = new ConcurrentHashMap<>();

    public Flow getOrCreate(FiveTuple tuple) {
        return flowTable.computeIfAbsent(tuple, k -> new Flow());
    }

    public synchronized void updateSni(FiveTuple tuple, String sni) {
        Flow flow = getOrCreate(tuple);
        if (flow.sni == null) {
            flow.sni = sni;
            flow.appType = AppType.fromSni(sni);
        }
    }

    public Map<FiveTuple, Flow> getFlowTable() {
        return flowTable;
    }

    public int size() {
        return flowTable.size();
    }
}