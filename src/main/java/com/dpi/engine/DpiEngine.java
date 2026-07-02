package com.dpi.engine;

import com.dpi.io.PcapReader;
import com.dpi.io.PcapWriter;
import com.dpi.model.Packet;
import com.dpi.report.ReportGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DpiEngine {

    private static final int NUM_WORKERS    = 4;
    private static final int QUEUE_CAPACITY = 1000;

    private final RuleManager rules;
    private final FlowTracker tracker;

    public DpiEngine(RuleManager rules) {
        this.rules   = rules;
        this.tracker = new FlowTracker();
    }

    @SuppressWarnings("unchecked")
    public void process(String inputFile, String outputFile) throws Exception {

        LinkedBlockingQueue<Packet>[] workerQueues = new LinkedBlockingQueue[NUM_WORKERS];
        for (int i = 0; i < NUM_WORKERS; i++) {
            workerQueues[i] = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        }

        LinkedBlockingQueue<Packet> outputQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        List<WorkerThread> workers = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(NUM_WORKERS);

        for (int i = 0; i < NUM_WORKERS; i++) {
            WorkerThread worker = new WorkerThread(
                i, workerQueues[i], outputQueue, tracker, rules);
            workers.add(worker);
            pool.submit(worker);
        }

        Thread outputWriter = new Thread(() -> {
            PcapWriter writer = new PcapWriter();
            try {
                writer.open(outputFile);
                while (true) {
                    Packet p = outputQueue.poll(200, TimeUnit.MILLISECONDS);
                    if (p == null) {
                        if (pool.isTerminated()) break;
                    } else {
                        writer.writePacket(p);
                    }
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        outputWriter.start();

        PcapReader reader = new PcapReader();
        if (!reader.open(inputFile)) {
            System.out.println("Failed to open: " + inputFile);
            return;
        }

        AtomicLong total = new AtomicLong(0);
        Packet packet;

        while ((packet = reader.readNextPacket()) != null) {
            total.incrementAndGet();
            int workerIdx = (packet.toFiveTuple().hashCode() & Integer.MAX_VALUE) % NUM_WORKERS;
            workerQueues[workerIdx].put(packet);
        }

        reader.close();

        for (LinkedBlockingQueue<Packet> q : workerQueues) {
            q.put(WorkerThread.POISON_PILL);
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        outputWriter.join();

        long fwd = workers.stream().mapToLong(w -> w.forwarded.get()).sum();
        long drp = workers.stream().mapToLong(w -> w.dropped.get()).sum();

        System.out.println("\nWorker Statistics:");
        for (WorkerThread w : workers) {
            System.out.println("  Worker-" + w.getId()
                + " processed: " + w.processed.get() + " packets");
        }

        ReportGenerator.print(tracker.getFlowTable(), total.get(), fwd, drp);
    }
}