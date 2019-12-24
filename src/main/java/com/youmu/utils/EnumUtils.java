package com.youmu.utils;

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