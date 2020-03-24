package com.youmu.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.youmu.common.AbstractCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: YOUMU
 * @Description: 枚举工具类
 * @Date: 2017/07/30
 */
public final class EnumUtils {
    private static final Logger LOG = LoggerFactory.getLogger(EnumUtils.class);

    private EnumUtils() {
    }

    /**
     * 使用枚举生成对应的map,重名后面的会覆盖掉前面的
     * @param enumClass 枚举类型
     * @param keyGetFunc 用枚举生成枚举key的方法
     * @param <E> 枚举类型
     * @param <K> key的类型
     * @return key->枚举的map
     */
    public static <E extends Enum<E>, K> Map<K, E> toMap(Class<E> enumClass, Function<E, K> keyGetFunc) {
        if (null == enumClass || null == keyGetFunc) {
            throw new IllegalArgumentException("缺少参数，enumClass或keyGetFunc");
        }
        E[] enumConstants = enumClass.getEnumConstants();
        Map<K, E> map = new HashMap<>(enumConstants.length);
        for (E enumConstant : enumConstants) {
            K key = keyGetFunc.apply(enumConstant);
            map.put(key, enumConstant);
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 根据code获取枚举
     * @param enumClass 哪个枚举类
     * @param code 哪个code
     * @param <T>
     * @return 查找的枚举
     */
    public static <T extends Enum & AbstractCode> Optional<T> get(Class<T> enumClass, int code) {
        if (!AbstractCode.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException(
                    enumClass.getCanonicalName() + " is not implements from AbstractCode");
        }
        return Optional.ofNullable(getByProperty(enumClass, code, (e) -> e.getCode()));
    }

    /**
     * 根据属性比较来
     * @param enumClass
     * @param ele
     * @param getMethod
     * @param <E>
     * @param <V>
     * @return
     */
    public static <E extends Enum, V> E getByProperty(Class<E> enumClass, V ele,
                                                      Function<E, V> getMethod) {
        return get(enumClass, ele, (e, v) -> getMethod.apply(e).equals(v));
    }

    public static <E extends Enum, V> E get(Class<E> enumClass, V ele,
                                            EqualsComparator<E, V> comparator) {
        if (null == comparator) {
            return null;
        }
        Enum[] es = enumClass.getEnumConstants();
        for (int i = 0; i < es.length; i++) {
            Enum e = es[i];
            if (comparator.equalsTo((E) e, ele)) {
                return (E) e;
            }
        }
        return null;
    }

    public static <E extends Enum, V> boolean contains(Class<E> enumClass, V ele,
                                                       EqualsComparator<E, V> comparator) {
        return null != get(enumClass, ele, comparator);
    }

    public interface EqualsComparator<V1, V2> {

        boolean equalsTo(V1 value1, V2 value2);

    }

}