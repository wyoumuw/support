package com.youmu.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
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

    public static void setFieldForce(Field field, Object obj, Object value)
            throws IllegalAccessException {
        if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
            field.setAccessible(true);
        }
        field.set(obj, value);
    }

    public static Object getFieldForce(Field field, Object obj) throws IllegalAccessException {
        if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(obj);
    }

    public static <S, T> T copyProperties(S source, Class<T> tClass,
            TriFunction<Field, Field, Object, Object> converter) {
        final T t;
        try {
            t = tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
        copyProperties(source, t, converter);
        return t;
    }

    public static <S, T> void copyProperties(S source, T target,
            TriFunction<Field, Field, Object, Object> converter) {
        copyProperties(source, target, defaultCopyPredicate, converter);
    }

    /**
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

}
