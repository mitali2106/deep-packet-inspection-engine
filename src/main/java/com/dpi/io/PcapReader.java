package com.dpi.io;

import com.dpi.model.Packet;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PcapReader {

    private FileInputStream fis;
    private boolean valid = false;

    public boolean open(String filePath) throws IOException {
        fis = new FileInputStream(filePath);

        byte[] globalHeader = fis.readNBytes(24);
        if (globalHeader.length != 24) return false;

        ByteBuffer buf = ByteBuffer.wrap(globalHeader);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int magic = buf.getInt();
        if (magic != 0xa1b2c3d4) return false;

        valid = true;
        return true;
    }

    public Packet readNextPacket() throws IOException {
        if (!valid) return null;

        byte[] headerBytes = fis.readNBytes(16);
        if (headerBytes.length != 16) return null;

        ByteBuffer header = ByteBuffer.wrap(headerBytes);
        header.order(ByteOrder.LITTLE_ENDIAN);

        long tsSec    = header.getInt() & 0xFFFFFFFFL;
        long tsUsec   = header.getInt() & 0xFFFFFFFFL;
        int capturedLen = header.getInt();
        header.getInt();

        byte[] data = fis.readNBytes(capturedLen);
        if (data.length != capturedLen) return null;

        Packet packet    = new Packet();
        packet.rawData   = data;
        packet.length    = capturedLen;
        packet.timestamp = tsSec * 1000 + tsUsec / 1000;

        return packet;
    }

    public void close() throws IOException {
        if (fis != null) fis.close();
    }
}