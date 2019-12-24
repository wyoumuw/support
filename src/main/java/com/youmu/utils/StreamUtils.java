package com.youmu.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: YOUMU
 * @Description: java8 Stream相关
 * @Date: 2018/11/12
 */
public final class StreamUtils {

    private StreamUtils() {
    }

    /**
     * 获取collection的stream如果collection是null则返回空stream
     * @param collection 要获取stream的collection
     * @param <T> collection的内容
     * @return 非null的Stream
     */
    public static <T> Stream<T> ofNullable(Collection<T> collection) {
        if (null == collection) {
            return Stream.empty();
        }
        return collection.stream();
    }

    /**
     * 把流转换成map,如果出现相同的key时则后一个遍历到的value将会覆盖上一个
     * @param stream 流
     * @param keyMapper key生成器
     * @param valueMapper value生成器
     * @param <T> 流内容的类型
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @return K->V的map。如果stream为null则返回空map
     */
    public static <T, K, V> Map<K, V> toMapWithDuplicateKey(Stream<T> stream,
        Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        if (null == stream) {
            return new HashMap<>();
        }
        return stream.collect(Collectors.toMap(keyMapper, valueMapper, overrideMerger()));
    }

    /**
     * 做map相同key的值合并时，使用后加入的value覆盖上一个值
     * @param <V>
     * @return
     */
    public static <V> BinaryOperator<V> overrideMerger() {
        return (value1, value2) -> value2;
    }
}
