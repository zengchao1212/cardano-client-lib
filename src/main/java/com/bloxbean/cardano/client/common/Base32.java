package com.bloxbean.cardano.client.common;

import com.bloxbean.cardano.client.exception.AddressExcepion;

import java.io.ByteArrayOutputStream;

public class Base32 {
    private static byte[] convertBits(final byte[] in, final int inLen, final int fromBits,
                               final int toBits, final boolean pad) throws AddressExcepion {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inLen; i++) {
            int value = in[i] & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new AddressExcepion(
                        String.format("Input value '%X' exceeds '%d' bit size", value, fromBits));
            }
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0)
                out.write((acc << (toBits - bits)) & maxv);
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new AddressExcepion("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }

    public static byte[] encode(byte[] witnessProgram) throws AddressExcepion {
        return convertBits(witnessProgram, witnessProgram.length, 8, 5, true);
    }

    public static byte[] decode(byte[] witnessProgram) throws AddressExcepion {
        return convertBits(witnessProgram, witnessProgram.length, 5, 8, false);
    }
}
