package io.github.ibramsou.netty.messaging.api.option;

import java.util.LinkedHashMap;

public class OptionMap<K, V> extends Option<LinkedHashMap<K, V>> {

    protected OptionMap(LinkedHashMap<K, V> map) {
        super(map);
    }

    public OptionMap() {
        super(new LinkedHashMap<>());
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
    @SuppressWarnings("unchecked")
    public Option<LinkedHashMap<K, V>> copy() {
        return new OptionMap<>((LinkedHashMap<K, V>) this.value.clone());
    }
}