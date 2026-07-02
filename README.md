# Deep Packet Inspection Engine

A multi-threaded network packet analysis engine built in Java that inspects, classifies, and filters network traffic at the byte level.

## Features

- 4-worker ExecutorService producer-consumer pipeline with LinkedBlockingQueue
- Five-Tuple hashing (src IP, dst IP, src port, dst port, protocol) for accurate flow-state tracking
- Byte-level protocol parser across 4 network layers (Ethernet, IPv4, TCP, TLS)
- SNI extraction from encrypted HTTPS traffic
- Classifies 70%+ traffic into 20+ application signatures
- 3-tier rule-based blocking via ConcurrentHashMap

## Tech Stack

Java | ExecutorService | LinkedBlockingQueue | ByteBuffer | ConcurrentHashMap | Maven

## How to Run

```bash
mvn package -q
java -jar target/dpi-engine-java-1.0.jar input.pcap output.pcap --block-app YOUTUBE --block-app TIKTOK
```

## Sample Output
Total Packets  : 13,342
Forwarded      : 13,303
Dropped        : 39
Unique Flows   : 264
YOUTUBE  : 45 packets (BLOCKED)
GITHUB   : 503 packets
NETFLIX  : 67 packets
## Results on Real Traffic

Tested on real Wi-Fi capture — detected YouTube CDN traffic and blocked 45 packets across 264 unique flows.

## Status

✅ Complete