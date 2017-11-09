package com.youmu.utils;

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
     * @param getMethod 属性的get方法
     * @param <E> 枚举类型
     * @param <V> 属性类型
     * @return
     */
    public static <E extends Enum, V> E getByProperty(Class<E> enumClass, V ele,
            Function<E, V> getMethod) {
        if (null == getMethod) {
            return null;
        }
        return get(enumClass, ele, (e, v) -> getMethod.apply(e).equals(v));
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
            EqualsComparator<E, V> comparator) {
        return null != get(enumClass, ele, comparator);
    }

    public interface EqualsComparator<V1, V2> {

        boolean equalsTo(V1 value1, V2 value2);

    }

}