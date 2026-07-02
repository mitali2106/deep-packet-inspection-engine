package com.dpi.parser;

import java.util.Optional;

public class SniExtractor {

    public static Optional<String> extract(byte[] payload, int length) {
        if (payload == null || length < 6) return Optional.empty();

        if ((payload[0] & 0xFF) != 0x16) return Optional.empty();
        if ((payload[5] & 0xFF) != 0x01) return Optional.empty();

        int offset = 43;
        if (offset >= length) return Optional.empty();

        int sessionIdLen = payload[offset] & 0xFF;
        offset += 1 + sessionIdLen;
        if (offset + 2 >= length) return Optional.empty();

        int cipherSuitesLen = ((payload[offset] & 0xFF) << 8) | (payload[offset + 1] & 0xFF);
        offset += 2 + cipherSuitesLen;
        if (offset >= length) return Optional.empty();

        int compressionLen = payload[offset] & 0xFF;
        offset += 1 + compressionLen;
        if (offset + 2 >= length) return Optional.empty();

        int extensionsLen = ((payload[offset] & 0xFF) << 8) | (payload[offset + 1] & 0xFF);
        offset += 2;

        int extensionsEnd = offset + extensionsLen;

        while (offset + 4 <= extensionsEnd && offset + 4 <= length) {
            int extType = ((payload[offset] & 0xFF) << 8) | (payload[offset + 1] & 0xFF);
            int extLen  = ((payload[offset + 2] & 0xFF) << 8) | (payload[offset + 3] & 0xFF);
            offset += 4;

            if (extType == 0x0000) {
                if (offset + 5 > length) return Optional.empty();
                int sniLen = ((payload[offset + 3] & 0xFF) << 8) | (payload[offset + 4] & 0xFF);
                offset += 5;
                if (offset + sniLen > length) return Optional.empty();
                return Optional.of(new String(payload, offset, sniLen));
            }

            offset += extLen;
        }

        return Optional.empty();
    }
}