package io.github.ibramsou.netty.messaging.api.session;

import io.github.ibramsou.netty.messaging.api.option.Option;
import io.github.ibramsou.netty.messaging.api.option.OptionList;
import io.github.ibramsou.netty.messaging.api.option.OptionMap;
import io.github.ibramsou.netty.messaging.api.option.OptionValue;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface SessionConfig {

    <T> SessionConfig set(OptionValue<T> option, T value);

    <T> T get(OptionValue<T> option);

    <T> SessionConfig add(OptionList<T> option, T value);

    <T> List<T> get(OptionList<T> option);

    <K, V> SessionConfig set(OptionMap<K, V> option, K key, V value);

    <K, V> V remove(OptionMap<K, V> option, K key);

    <K, V> Map<K, V> get(OptionMap<K, V> option);

    SessionConfig clear(OptionList<?> option);

    <K, V> SessionConfig clear(OptionMap<K, V> option);

    SessionConfig toDefault(Option<?> option);

}
