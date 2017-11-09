package com.youmu.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

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

    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    /**
     * 获取目标对象（递归
     * @param proxy 代理对象
     * @return 目标对象
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
     * 获取目标对象
     * @param proxy 代理对象
     * @return 目标对象
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
        if (ClassUtils.isCglibProxy(proxy.getClass())) {
            return getCglibTarget(proxy);
        }
        return proxy;
    }

    /**
     * 获取cglib的目标对象，目前只支持spring
     * @param proxy 代理对象
     * @return 目标对象
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
     * 获取jdk的目标对象，目前只支持spring
     * @param proxy 代理对象
     * @return 目标对象
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
     * 设置obj的field的值
     * @param field 属性域
     * @param obj 获取的对象
     * @param value obj的field的值
     * @throws IllegalAccessException
     */
    public static void setFieldForce(Field field, Object obj, Object value)
            throws IllegalAccessException {
        if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
            field.setAccessible(true);
        }
        field.set(obj, value);
    }

    /**
     * 获取obj的field的值
     * @param field 属性域
     * @param obj 获取的对象
     * @return obj的field的值
     * @throws IllegalAccessException
     */
    public static Object getFieldForce(Field field, Object obj) throws IllegalAccessException {
        if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(obj);
    }

    public static <S, T> T copyProperties(S source, Class<T> tClass,
            TriFunction<Field/** source **/
                    , Field/** target **/
                    , Object/** value **/
                    , Object/** return **/
            > converter) {
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

    /**
     * 属性拷贝从source到target，同名并且可以转换的属性会被拷贝，不拷贝static的属性
     * @param source 源对象
     * @param target 目标对象
     * @param converter 属性转换器，可以自行对属性类型进行转换
     * @param <S> 源类型
     * @param <T> 目标类型
     */
    public static <S, T> void copyProperties(S source, T target,
            TriFunction<Field/** source **/
                    , Field/** target **/
                    , Object/** value **/
                    , Object/** return **/
            > converter) {
        if (null == target || null == source) {
            throw new NullPointerException("target || source cannot be null!");
        }
        ReflectionUtils.doWithFields(source.getClass(), field -> {
            Field tF = ReflectionUtils.findField(target.getClass(), field.getName());
            if (null == tF) {
                return;
            }
            // 不拷贝静态和final内容
            if (Modifier.isStatic(tF.getModifiers()) || Modifier.isFinal(tF.getModifiers())
                    || Modifier.isStatic(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())) {
                return;
            }
            if (!Modifier.isPublic(tF.getModifiers())) {
                tF.setAccessible(true);
            }
            if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
            }
            Object rtn = field.get(source);
            if (null != converter) {
                rtn = converter.apply(field, tF, rtn);
            }
            if (null == rtn) {
                return;
            }
            if (ClassUtils.isAssignable(tF.getType(), rtn.getClass())) {
                tF.set(target, rtn);
            }
        });
    }

    public static interface TriFunction<P1, P2, P3, R> {
        public R apply(P1 p1, P2 p2, P3 p3);
    }

}
