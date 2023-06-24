package io.github.ibramsou.netty.messaging.api.util;

public class BufferUtil {

    private static final int[] VARINT_EXACT_BYTE_LENGTHS = new int[33];

    static {
        for (int i = 0; i <= 32; i++) {
            VARINT_EXACT_BYTE_LENGTHS[i] = (int) Math.ceil((31.0D - (i - 1)) / 7.0D);
        }
        VARINT_EXACT_BYTE_LENGTHS[32] = 1;
    }

    public static int getVarIntSize(int value) {
        return VARINT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    public static int getVarIntSize_(int length) {
        for(int i = 1; i < 5; ++i) {
            if ((length & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 5;
    }
}
