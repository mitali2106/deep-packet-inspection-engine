package com.dpi.io;

import com.dpi.model.Packet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PcapWriter {

    private FileOutputStream fos;

    public void open(String filePath) throws IOException {
        fos = new FileOutputStream(filePath);
        writeGlobalHeader();
    }

    private void writeGlobalHeader() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(24);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(0xa1b2c3d4);
        buf.putShort((short) 2);
        buf.putShort((short) 4);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(65535);
        buf.putInt(1);
        fos.write(buf.array());
    }

    public void writePacket(Packet packet) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(16);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.putInt((int)(packet.timestamp / 1000));
        header.putInt((int)((packet.timestamp % 1000) * 1000));
        header.putInt(packet.length);
        header.putInt(packet.length);
        fos.write(header.array());
        fos.write(packet.rawData, 0, packet.length);
    }

    public void close() throws IOException {
        if (fos != null) fos.close();
    }
}