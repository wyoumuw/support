package com.youmu;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

/**
 * TODO
 *
 * @version V1.0
 * @since 2019-09-02 17:21
 */
public class UnitTestUtilsTest {

    @Test
    public void generate() {
        System.out.println(generate(Object.class));
    }

    public String generate(Class clazz) {
        String className = clazz.getSimpleName();
        String varName = StringUtils.uncapitalize(className);
        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append(className).append(" ").append(varName).append(" = new ").append(className).append("();\n");
        ReflectionUtils.doWithFields(clazz, f -> {
            String val = randomValue(f);
//            if (null == val) {
//                stringBuilder.append(generate(f.getType()));
//                val = StringUtils.uncapitalize(f.getType().getSimpleName());
//            }
            stringBuilder.append(varName).append(".").append("set").append(StringUtils.capitalize(f.getName()))
                .append("(").append(val).append(");\n");
        });
        return stringBuilder.toString();
    }

    private String randomValue(Field field) {
        Class<?> type = field.getType();
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
            return "\"" + field.getName() + "\"";
        }  else {
            return null;
        }
    }

    public static void main(String[] args) throws NoSuchFieldException {
        ParameterizedType data = (ParameterizedType) Object.class.getDeclaredField("data")
            .getGenericType();
        for (Type actualTypeArgument : data.getActualTypeArguments()) {
            System.out.println(actualTypeArgument instanceof Class);
        }
        System.out.println(data.getActualTypeArguments());
    }

    private static <T> T randomModel(Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            ReflectionUtils.doWithFields(clazz, f -> {
                Class<?> type = f.getType();
                f.setAccessible(true);
                if (type.isPrimitive()) {
                    return;
                } else if (type == Integer.class) {
                    f.set(t, 0);
                } else if (type == Long.class) {
                    f.set(t, 0L);
                } else if (type == Double.class) {
                    f.set(t, 0D);
                } else if (type == Float.class) {
                    f.set(t, 0F);
                } else if (type == Boolean.class) {
                    f.set(t, false);
                } else if (type == Date.class) {
                    f.set(t, new Date());
                } else if (type == String.class) {
                    f.set(t, f.getName());
                } else {
                    f.set(t, null);
                }
            });
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
