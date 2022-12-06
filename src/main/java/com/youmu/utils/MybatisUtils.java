package com.youmu.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class MybatisUtils {

    public static String getter(String field) {
        return "get" + StringUtils.capitalize(field);
    }

    public static String setter(String field) {
        return "set" + StringUtils.capitalize(field);
    }

    public static List<IntrospectedColumn> getNullableList(IntrospectedTable introspectedTable) {
        return introspectedTable.getNonPrimaryKeyColumns().stream().filter(IntrospectedColumn::isNullable).collect(Collectors.toList());
    }

    public static List<IntrospectedColumn> getNotNullableList(IntrospectedTable introspectedTable) {
        return introspectedTable.getNonPrimaryKeyColumns().stream().filter(c -> !c.isNullable()).collect(Collectors.toList());
    }

    public static String randomValue(IntrospectedColumn introspectedColumn) throws ClassNotFoundException {
        Class<?> type = Class.forName(introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName());
        if (type == Integer.class) {
            return "0";
        } else if (type == Long.class) {
            return "0L";
        } else if (type == Double.class) {
            return "0D";
        } else if (type == Float.class) {
            return "0F";
        } else if (type == Boolean.class) {
            return "false";
        } else if (type == Date.class) {
            return "new Date()";
        } else if (type == String.class) {
            return "\"" + introspectedColumn.getJavaProperty() + "\"";
        } else {
            return null;
        }
    }
}
