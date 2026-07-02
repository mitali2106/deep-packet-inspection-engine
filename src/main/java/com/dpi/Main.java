package com.dpi;

import com.dpi.engine.DpiEngine;
import com.dpi.engine.RuleManager;
import com.dpi.model.AppType;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java -jar dpi-engine.jar <input.pcap> <output.pcap> [--block-app APP] [--block-ip IP] [--block-domain DOMAIN]");
            return;
        }

        String inputFile  = args[0];
        String outputFile = args[1];

        RuleManager rules = new RuleManager();

        for (int i = 2; i < args.length; i++) {
            switch (args[i]) {
                case "--block-app"    -> rules.blockApp(AppType.valueOf(args[++i].toUpperCase()));
                case "--block-ip"     -> rules.blockIp(args[++i]);
                case "--block-domain" -> rules.blockDomain(args[++i]);
            }
        }

        DpiEngine engine = new DpiEngine(rules);
        engine.process(inputFile, outputFile);
    }
}