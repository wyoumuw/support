package com.youmu.poi.excel.annotation;

import java.lang.annotation.*;

/**
 * Created by wyoumuw on 2019/9/3.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {
    /**
     * 表头名称
     *
     * @return
     */
    String value() default defaultValue.NULL_VALUE;

    /**
     * 列号从0开始
     *
     * @return
     */
    int index() default defaultValue.NULL_INDEX;

    //默认值
    class defaultValue {
        public static final String NULL_VALUE = "";
        public static final int NULL_INDEX = -1;
    }
}
