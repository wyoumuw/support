package com.youmu.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.persistence.Id;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class DbUtils {

    private static final String WILDCARD = "%";

    private DbUtils() {
    }

    /**
     * 设置主键的值
     * @param obj
     * @param primaryKey
     */
    public static void setPrimaryKeyValue(Object obj, Object primaryKey) {
        if (null == obj) {
            return;
        }
        ReflectionUtils.doWithFields(obj.getClass(), field -> {
            if (field.isAnnotationPresent(Id.class)) {
                if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(obj, primaryKey);
            }
        });
    }


    /**
     * 创建默认包含未删除的Example
     * @param clazz
     * @return
     */
    public static Example.Builder newExampleBuilder(Class<?> clazz) {
        return Example.builder(clazz);
    }

    /**
     * 使用BaseQueryModel创建Example.Builder
     * @param @NotNull queryModel
     * @return
     */
    @SuppressWarnings("unckeck")
    public static Example.Builder newExampleBuilder(Class<?> clazz, QueryModel queryModel) {
        Example.Builder builder = new Example.Builder(clazz,false);
        builder.select(queryModel.getProperties());
        if (StringUtils.isNotBlank(queryModel.getOrderBy())) {
            if (queryModel.isAsc()) {
                builder.orderByAsc(queryModel.getOrderBy());
            } else {
                builder.orderByDesc(queryModel.getOrderBy());
            }
        }
        return builder;
    }

    /**
     * 从list里获取第一条数据
     * @param @NotNull pageInfo
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getOne(List<T> list) {
        return CollectionUtils.isEmpty(list) ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    /**
     * 给Example.Builder设置为collection的属性
     * @param
     * @param property
     * @param collection
     */
    public static void setCollectionProp(Example example, String property,
                                         Collection<?> collection) {
        if (null == collection) {
            return;
        }
        if (1 == collection.size()) {
            Object obj = collection.iterator().next();
            if (null != obj) {
                example.createCriteria().andEqualTo(property, obj);
            }
        } else if (collection.isEmpty()) {
            // 如果是空其实并不想查询出结果也就是 select x from t where a in ()
            // 但是此行为在mybatis里是被忽略的也就是会变成 select x from t where a in 报错 查看github issues #415
            // 解决方案是如果是空数组则其实是希望什么都不查出来，则给一个deleteTime为乱七八糟的数据则可以解决
            example.createCriteria().andCondition("1=1");
        } else {
            example.createCriteria().andIn(property, collection);
        }
    }

    /**
     * 给Example.Builder设置一般的属性,如果非String的会判空，如果是String的会判断{@param }
     * @param builder builder
     * @param property 属性名
     * @param value 值
     * @param allowStrBlank 是否允许blankStr，如果true则不会判断isBlank
     */
    public static void setEqualToProp(Example.Builder builder, String property, Object value,
                                      boolean allowStrBlank) {
        Object val = value;
        if (null == val) {
            return;
        }
        if (!allowStrBlank && val instanceof String && StringUtils.isBlank((String) val)) {
            return;
        }
        builder.andWhere(Sqls.custom().andEqualTo(property, val));
    }

    /**
     * 查看
     * {@link DbUtils#setEqualToProp(tk.mybatis.mapper.entity.Example.Builder, String, Object, boolean)}
     * 不允许blank str
     * @param builder
     * @param property
     * @param value
     */
    public static void setEqualToProp(Example.Builder builder, String property, Object value) {
        setEqualToProp(builder, property, value, false);
    }

    /**
     * 生成不等于查询条件
     * @param builder builder
     * @param property 属性
     * @param value 值
     * @param allowStrBlank 是否允许blankStr，如果true则不会判断isBlank
     */
    public static void setNotEqualToProp(Example.Builder builder, String property, Object value,
                                         boolean allowStrBlank) {
        Object val = value;
        if (null == val) {
            return;
        }
        if (!allowStrBlank && val instanceof String && StringUtils.isBlank((String) val)) {
            return;
        }
        builder.andWhere(Sqls.custom().andNotEqualTo(property, val));
    }

    /**
     * 生成不等于查询条件，默认空字符串忽略
     * @param builder builder
     * @param property 属性
     * @param value 值
     */
    public static void setNotEqualToProp(Example.Builder builder, String property, Object value) {
        setNotEqualToProp(builder, property, value, false);
    }

    /**
     * 生成like查询条件，前后均包含统配符
     * @param builder builder
     * @param property 属性名
     * @param value 查询值，仅限字符串
     * @param allowStrBlank 是否允许blankStr，如果true则不会判断isBlank
     */
    public static void setLikeProp(Example.Builder builder, String property, String value,
                                   boolean allowStrBlank) {
        if (null == value) {
            // 非字符串没有like
            return;
        }
        if (!allowStrBlank && StringUtils.isBlank(value)) {
            // 不允许空白字符串
            return;
        }
        builder.andWhere(Sqls.custom().andLike(property, getLikeStr(value)));
    }

    public static String getLikeStr(String value) {
        return WILDCARD + value + WILDCARD;
    }

    /**
     * 生成like查询条件，前后均包含统配符，不允许空白字符串
     * @param builder builder
     * @param property 属性名
     * @param value 查询值，仅限字符串
     */
    public static void setLikeProp(Example.Builder builder, String property, String value) {
        setLikeProp(builder, property, value, false);
    }

    /**
     * 生成like查询条件，在value后包含统配符，表示以value为开始的模糊查询
     * @param builder builder
     * @param property 属性名
     * @param value 查询值，仅限字符串
     * @param allowStrBlank 是否允许blankStr，如果true则不会判断isBlank
     */
    public static void setLikePropPrefix(Example.Builder builder, String property, String value,
                                         boolean allowStrBlank) {
        if (null == value) {
            // 非字符串没有like
            return;
        }
        if (!allowStrBlank && StringUtils.isBlank(value)) {
            // 不允许空白字符串
            return;
        }
        builder.andWhere(Sqls.custom().andLike(property, getLikeStrPrefix(value)));
    }

    public static String getLikeStrPrefix(String value) {
        return value + WILDCARD;
    }

    /**
     * 生成前缀like查询条件，后面包含统配符，不允许空白字符串
     * @param builder builder
     * @param property 属性名
     * @param value 查询值，仅限字符串
     */
    public static void setLikePropPrefix(Example.Builder builder, String property, String value) {
        setLikePropPrefix(builder, property, value, false);
    }

    /**
     * 自己拼sql条件 相当于property+condition
     * @param builder example构建器
     * @param property 属性名
     * @param condition 条件
     */
    public static void setCondition(Example.Builder builder, String property, String condition) {
        Sqls sqls = Sqls.custom();
        sqls.getCriteria().getCriterions().add(new Sqls.Criterion(property, condition, "and"));
        builder.andWhere(sqls);
    }

    /**
     * 生成区间查询条件，默认前闭后开，null值不生成查询条件
     * @param builder builder
     * @param property 属性名
     * @param beginValue 区间开始值
     * @param endValue 区间结束值
     */
    public static void setIntervalProp(Example.Builder builder, String property, Object beginValue,
                                       Object endValue) {
        if (null != beginValue) {
            builder.andWhere(Sqls.custom().andGreaterThanOrEqualTo(property, beginValue));
        }
        if (null != endValue) {
            builder.andWhere(Sqls.custom().andLessThan(property, endValue));
        }
    }

    /**
     * 生成null值查询条件，条件为null时不生成条件
     * @param builder builder
     * @param property 属性名
     * @param isNull 是否为null
     */
    public static void setNullProp(Example.Builder builder, String property, Boolean isNull) {
        if (null == isNull) {
            // null时不处理
            return;
        }
        if (isNull) {
            builder.andWhere(Sqls.custom().andIsNull(property));
        } else {
            builder.andWhere(Sqls.custom().andIsNotNull(property));
        }
    }

    /**
     * 自定义删除字段property和值value创建Example
     * @param clazz 实体对象clazz
     * @param property 自定义删除字段 当property为empty时，默认删除字段是BaseObject.PROP_DELETE_TIME，value为BaseObject.VAL_NOT_DELETED表示未删除;
     * @param value 表示删除的值 当value为null时，默认删除字段是BaseObject.PROP_DELETE_TIME，value为BaseObject.VAL_NOT_DELETED表示未删除;
     * @return Example.Builder
     */
    public static Example.Builder newExampleBuilder(Class<?> clazz, String property, Object value) {
        if (StringUtils.isEmpty(property) || null == value) {
           return newExampleBuilder(clazz);
        }
        return Example.builder(clazz).andWhere(Sqls.custom().andEqualTo(property, value));
    }

    /**
     * 使用QueryModel和自定义删除字段property和值value创建Example.Builder
     * @param queryModel 主要是properties：限制返回列名称列表 orderBy：获取排序字段 isAsc： 排序时是否正序，默认正序
     * @param property 自定义删除字段 当property为empty时，默认删除字段是BaseObject.PROP_DELETE_TIME，value为BaseObject.VAL_NOT_DELETED表示未删除;
     * @param value 表示删除的值 当value为null时，默认删除字段是BaseObject.PROP_DELETE_TIME，value为BaseObject.VAL_NOT_DELETED表示未删除;
     * @return Example.Builder
     */
    @SuppressWarnings("unckeck")
    public static Example.Builder newExampleBuilder(Class<?> clazz, QueryModel queryModel, String property, Object value) {
        if (StringUtils.isEmpty(property) || null == value) {
           return newExampleBuilder(clazz, queryModel);
        }
        Example.Builder builder = Example.builder(clazz);
        builder.select(queryModel.getProperties());
        builder.andWhere(Sqls.custom().andEqualTo(property, value));
        if (StringUtils.isNotBlank(queryModel.getOrderBy())) {
            if (queryModel.isAsc()) {
                builder.orderByAsc(queryModel.getOrderBy());
            } else {
                builder.orderByDesc(queryModel.getOrderBy());
            }
        }
        return builder;
    }
}
