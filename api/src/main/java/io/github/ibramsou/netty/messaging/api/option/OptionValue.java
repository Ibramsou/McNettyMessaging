package io.github.ibramsou.netty.messaging.api.option;

public class OptionValue<V> extends Option<V> {

    public OptionValue(V value) {
        super(value);
    }

    @Override
    public OptionValue<V> copy() {
        return new OptionValue<>(this.value);
    }
}
