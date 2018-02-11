package com.youmu.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

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
     * 根据属性获取枚举
     * @param enumClass 枚举类
     * @param ele 需要获取的属性的值
     * @param getterMethod 属性的get方法
     * @param <E> 枚举类型
     * @param <V> 属性类型
     * @return
     */
    public static <E extends Enum, V> Optional<E> getByProperty(Class<E> enumClass, V ele,
            Function<E, V> getterMethod) {
        if (null == getterMethod) {
            return Optional.empty();
        }
        return get(enumClass, ele, (e, v) -> Objects.equals(getterMethod.apply(e), v));
    }

    /**
     * 基于比较器获取枚举
     * @param enumClass 枚举类
     * @param ele 需要获取的属性的值
     * @param comparator 比较器
     * @param <E> 枚举类型
     * @param <V> 属性类型
     * @return
     */
    public static <E extends Enum, V> Optional<E> get(Class<E> enumClass, V ele,
            BiPredicate<E, V> comparator) {
        if (null == comparator) {
            return Optional.empty();
        }
        Enum[] es = enumClass.getEnumConstants();
        E ret = null;
        for (int i = 0; i < es.length; i++) {
            Enum e = es[i];
            if (comparator.test((E) e, ele)) {
                ret = (E) e;
                break;
            }
        }
        return Optional.ofNullable(ret);
    }

    /**
     * 使用比较器判断是否有该枚举
     * @param enumClass 枚举类
     * @param ele 需要获取的属性的值
     * @param comparator 比较器
     * @param <E> 枚举类型
     * @param <V> 属性类型
     * @return
     */
    public static <E extends Enum, V> boolean contains(Class<E> enumClass, V ele,
            BiPredicate<E, V> comparator) {
        return get(enumClass, ele, comparator).isPresent();
    }

}