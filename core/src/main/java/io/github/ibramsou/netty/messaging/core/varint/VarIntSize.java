package io.github.ibramsou.netty.messaging.core.varint;

public class VarIntSize {

    private static final int[] VAR_INT_EXACT_BYTE_LENGTHS = new int[33];

    static {
        for (int i = 0; i <= 32; i++) {
            VAR_INT_EXACT_BYTE_LENGTHS[i] = (int) Math.ceil((31.0D - (i - 1)) / 7.0D);
        }
        VAR_INT_EXACT_BYTE_LENGTHS[32] = 1;
    }

    public static int getOptimizedVarIntSize(int value) {
        return VAR_INT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    public static int getVarIntSize(int length) {
        for(int i = 1; i < 5; ++i) {
            if ((length & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 5;
    }
}
