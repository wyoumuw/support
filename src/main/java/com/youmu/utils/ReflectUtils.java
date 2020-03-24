package com.youmu.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.youmu.exception.UnsupportedTargetException;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/08
 */
public abstract class ReflectUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    private ReflectUtils() {
    }

    // 默认拷贝判断，sourceField和targetField都不是final,static,synthetic的才进行拷贝
    private static final BiPredicate<Field, Field> defaultCopyPredicate = (sourceField,
            targetField) -> !Modifier.isStatic(sourceField.getModifiers())
                    && !Modifier.isFinal(sourceField.getModifiers()) && !sourceField.isSynthetic()
                    && !Modifier.isStatic(targetField.getModifiers())
                    && !Modifier.isFinal(targetField.getModifiers()) && !targetField.isSynthetic();

    /**
     * 递归从代理对象中获取源对象
     * @param proxy
     * @return
     * @throws Exception
     */
    public static Object getTargetDeep(Object proxy) throws Exception {
        Object target = proxy;
        while (true) {
            Object t = getTarget(target);
            if (null == target || t == target) {
                return target;
            }
            target = t;
        }
    }

    /**
     * 从代理对象中获取源对象
     * @param proxy
     * @return
     * @throws Exception
     */
    public static Object getTarget(Object proxy) throws Exception {
        if (null == proxy) {
            return null;
        }
        // 如果是jdk的proxy
        if (Proxy.isProxyClass(proxy.getClass())) {
            return getJdkTarget(proxy);
        }
        // 如果是cglib的proxy
        if (ClassUtils.isCglibProxyClass(proxy.getClass())) {
            return getCglibTarget(proxy);
        }
        return proxy;
    }

    /**
     * 获取Cglib代理对象中获取源对象(只支持spring实现
     * @param proxy
     * @return
     * @throws Exception
     */
    public static Object getCglibTarget(Object proxy) throws Exception {
        // 目前只支持spring的代理
        if (proxy instanceof SpringProxy) {
            Field h = ReflectionUtils.findField(proxy.getClass(), "CGLIB$CALLBACK_0");
            Object proxyFactoryBean = getFieldForce(h, proxy);
            Field advised = ReflectionUtils.findField(proxyFactoryBean.getClass(), "advised");
            return ((AdvisedSupport) getFieldForce(advised, proxyFactoryBean)).getTargetSource()
                    .getTarget();
        }
        throw new UnsupportedTargetException(
                "unsupported cglib proxy class:" + proxy.getClass().getName());
    }

    /**
     * 获取jdk代理对象中获取源对象(只支持spring实现
     * @param proxy
     * @return
     * @throws Exception
     */
    public static Object getJdkTarget(Object proxy) throws Exception {
        Class proxyClass = proxy.getClass();
        Field h = ReflectionUtils.findField(proxyClass, "h");
        if (proxy instanceof SpringProxy) {
            Object aopProxy = getFieldForce(h, proxy);
            Field advised = ReflectionUtils.findField(aopProxy.getClass(), "advised");
            AdvisedSupport advisedSupport = (AdvisedSupport) getFieldForce(advised, aopProxy);
            return advisedSupport.getTargetSource().getTarget();
        }
        return getFieldForce(h, proxy);
    }

    /**
     * 给field设值，自动完成accessible
     * @param field
     * @param obj
     * @param value
     */
    public static void setFieldForce(Field field, Object obj, Object value) {
        if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "Not allowed to access field '" + field.getName() + "': " + e);
        }
    }

    /**
     * 根据属性名设值，自动完成accessible
     * @param propertyName 属性名
     * @param obj 设值的对象
     * @param value 值
     */
    public static void setFieldForce(String propertyName, Object obj, Object value) {
        Field field = ReflectionUtils.findField(obj.getClass(), propertyName);
        setFieldForce(field, obj, value);
    }

    /**
     * 从field中获取值
     * @param field
     * @param obj
     * @return
     */
    public static Object getFieldForce(Field field, Object obj) {
        if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "Not allowed to access field '" + field.getName() + "': " + e);
        }
    }

    /**
     * 根据属性名获取值
     * @param propertyName 属性名
     * @param obj 设值的对象
     * @return
     */
    public static Object getFieldForce(String propertyName, Object obj) {
        Field field = ReflectionUtils.findField(obj.getClass(), propertyName);
        return getFieldForce(field, obj);
    }

    /**
     * 通过source生成tClass的对象，属性会被拷贝过去，可以通过converter来参与类型的转换
     * @param source
     * @param tClass
     * @param converter
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> T copyProperties(S source, Class<T> tClass,
            TriFunction<Field, Field, Object, Object> converter) {
        final T t;
        try {
            t = tClass.getConstructor().newInstance();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException
                | InstantiationException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
        copyProperties(source, t, converter);
        return t;
    }

    /**
     * 通过source生成tClass的对象，属性会被拷贝过去
     * look{@link ReflectUtils#copyProperties(java.lang.Object, java.lang.Class, TriFunction)}
     * @param source
     * @param tClass
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> T copyProperties(S source, Class<T> tClass) {
        return copyProperties(source, tClass, null);
    }

    /**
     * 属性拷贝source->target，可以通过converter来参与类型的转换
     * @param source
     * @param target
     * @param converter
     * @param <S>
     * @param <T>
     */
    public static <S, T> void copyProperties(S source, T target,
            TriFunction<Field, Field, Object, Object> converter) {
        copyProperties(source, target, defaultCopyPredicate, converter);
    }

    /**
     * 属性拷贝source->target look
     * {@link ReflectUtils#copyProperties(java.lang.Object, java.lang.Object, TriFunction)}
     * @param source
     * @param target
     * @param <S>
     * @param <T>
     */
    public static <S, T> void copyProperties(S source, T target) {
        copyProperties(source, target, null);
    }

    /**
     * 属性拷贝source->target， 可以通过copyPredicate（泛型参数1：源field，泛型参数2：目标field）来判断是否拷贝此属性，
     * 可以通过converter（泛型参数1：源field，泛型参数2：目标field，泛型参数3：要传给目标的值，泛型4：返回值类型）来参与类型的转换
     * @param source
     * @param target
     * @param copyPredicate<sourceField,targetField> return true if copy,or ignore
     *            this pair field copy
     * @param converter<sourceField,targetField,sourceValue,returnValue>
     * @param <S> sourceType
     * @param <T> targetType
     */
    public static <S, T> void copyProperties(S source, T target,
            BiPredicate<Field, Field> copyPredicate,
            TriFunction<Field, Field, Object, Object> converter) {
        if (null == target || null == source) {
            throw new NullPointerException("target || source cannot be null!");
        }
        org.springframework.util.ReflectionUtils.doWithFields(source.getClass(), field -> {
            Field tF = org.springframework.util.ReflectionUtils.findField(target.getClass(),
                    field.getName());
            if (null == tF) {
                return;
            }
            // 默认不拷贝静态和final和synthetic内容
            if (!Optional.ofNullable(copyPredicate).orElse(defaultCopyPredicate).test(field, tF)) {
                return;
            }
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.makeAccessible(tF);
            Object rtn = field.get(source);
            if (null != converter) {
                rtn = converter.apply(field, tF, rtn);
            }
            if (null == rtn || ClassUtils.isAssignable(tF.getType(), rtn.getClass())) {
                tF.set(target, rtn);
            }
        });
    }

    public static interface TriFunction<P1, P2, P3, R> {
        public R apply(P1 p1, P2 p2, P3 p3);
    }

    /**
     * 可以顺序处理多个转换器
     */
    public static class ConvertersFunction implements TriFunction<Field, Field, Object, Object> {
        List<TriFunction<Field, Field, Object, Object>> converters = new ArrayList<>();

        public ConvertersFunction(TriFunction<Field, Field, Object, Object>... converters) {
            if (null == converters) {
                return;
            }
            for (int i = 0; i < converters.length; i++) {
                TriFunction<Field, Field, Object, Object> converter = converters[i];
                this.converters.add(converter);
            }
        }

        @Override
        public Object apply(Field field, Field field2, Object o) {
            Object rtn = o;
            for (TriFunction<Field, Field, Object, Object> converter : converters) {
                rtn = converter.apply(field, field2, rtn);
            }
            return rtn;
        }
    }

}
