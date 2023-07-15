package io.github.ibramsou.netty.messaging.api.option;

import java.util.ArrayList;
import java.util.List;

public class OptionList<T> extends Option<List<T>> {

    public OptionList() {
        this(new ArrayList<>());
    }

    OptionList(List<T> value) {
        super(value);
    }

    @Override
    public OptionList<T> copy() {
        return new OptionList<>(new ArrayList<>());
    }

    

    public OptionList<T> add(T value) {
        this.value.add(value);
        return this;
    }

    public OptionList<T> clear() {
        this.value.clear();
        return this;
    }
}
