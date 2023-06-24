package io.github.ibramsou.netty.messaging.api.option;

import java.util.HashMap;
import java.util.Map;

public class OptionMap<K, V> extends Option<Map<K, V>> {

    protected OptionMap(Map<K, V> map) {
        super(map);
    }

    public OptionMap() {
        super(new HashMap<>());
    }

    public final V remove(K key) {
        return this.value.remove(key);
    }

    public OptionMap<K, V> set(K key, V value) {
        this.value.put(key, value);
        return this;
    }

    public OptionMap<K, V> clear() {
        this.value.clear();
        return this;
    }

    @Override
    public Option<Map<K, V>> copy() {
        return new OptionMap<>(this.value);
    }
}