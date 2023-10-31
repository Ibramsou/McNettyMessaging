package io.github.ibramsou.netty.messaging.api.packet.impl;

import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;

public abstract class SkippingPacket extends MessagingPacket {

    @Override
    public final boolean shouldRead() {
        return false;
    }
}
