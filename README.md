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
Java | ExecutorService | LinkedBlockingQueue | ByteBuffer | ConcurrentHashMap

## Status
🚧 Active development — core pipeline and protocol parser implemented.
