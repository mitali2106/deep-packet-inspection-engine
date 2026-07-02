package com.dpi.engine;

import com.dpi.model.Flow;
import com.dpi.model.FiveTuple;
import com.dpi.model.Packet;
import com.dpi.parser.PacketParser;
import com.dpi.parser.SniExtractor;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class WorkerThread implements Runnable {

    public static final Packet POISON_PILL = new Packet();

    private final int id;
    private final LinkedBlockingQueue<Packet> inputQueue;
    private final LinkedBlockingQueue<Packet> outputQueue;
    private final FlowTracker tracker;
    private final RuleManager rules;

    public final AtomicLong processed = new AtomicLong(0);
    public final AtomicLong dropped   = new AtomicLong(0);
    public final AtomicLong forwarded = new AtomicLong(0);

    public WorkerThread(int id,
                        LinkedBlockingQueue<Packet> inputQueue,
                        LinkedBlockingQueue<Packet> outputQueue,
                        FlowTracker tracker,
                        RuleManager rules) {
        this.id          = id;
        this.inputQueue  = inputQueue;
        this.outputQueue = outputQueue;
        this.tracker     = tracker;
        this.rules       = rules;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = inputQueue.take();

                if (packet == POISON_PILL) break;

                processed.incrementAndGet();

                if (PacketParser.parse(packet)) {
                    FiveTuple tuple = packet.toFiveTuple();
                    Flow flow = tracker.getOrCreate(tuple);
                    flow.packetCount.incrementAndGet();

                    if (packet.payloadLength > 0) {
                        Optional<String> sni = SniExtractor.extract(
                            packet.payload, packet.payloadLength);
                        sni.ifPresent(s -> tracker.updateSni(tuple, s));
                    }

                    if (!flow.blocked) {
                        flow.blocked = rules.isBlocked(
                            packet.srcIp, flow.appType, flow.sni);
                    }

                    if (flow.blocked) {
                        dropped.incrementAndGet();
                        continue;
                    }
                }

                forwarded.incrementAndGet();
                outputQueue.put(packet);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getId() {
        return id;
    }
}