package io.github.ibramsou.netty.messaging.core.varint;

@FunctionalInterface
public interface VarIntFunction {

    VarIntFunction DEFAULT_FUNCTION = VarIntSize::getVarIntSize;
    VarIntFunction OPTIMIZED_FUNCTION = VarIntSize::getOptimizedVarIntSize;

    int apply(int length);
}
