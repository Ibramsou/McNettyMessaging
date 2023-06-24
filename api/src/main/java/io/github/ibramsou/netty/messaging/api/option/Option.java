package io.github.ibramsou.netty.messaging.api.option;

public abstract class Option<T> {

    protected T value;

    public Option(T value) {
        this.value = value;
    }

    public final T getValue() {
        return value;
    }

    public abstract Option<T> copy();

    public final Option<T> value(T value) {
        this.value = value;
        return this;
    }
}
