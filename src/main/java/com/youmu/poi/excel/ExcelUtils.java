package com.youmu.poi.excel;

import com.youmu.poi.excel.annotation.ExcelColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyoumuw on 2019/9/3.
 */
public class ExcelUtils {

    private static final Map<Class<?>, Field[]> CACHE_FIELDS = new ConcurrentReferenceHashMap<>(512);

    /**
     * @param workbook  哪张表,
     * @param clazz     要读成的类型
     * @param hasHeader 是否涵盖表头，如果有表头的话则会跳过表头读取，使用ExcelColumn.value读取，如果value是空则使用index读取，如果index为-1则不填充字段。
     *                  如果没表头只会使用index去解析。
     * @param <T>       要读成的类型
     * @return
     */
    public static <T> List<T> read(Workbook workbook, Class<T> clazz, boolean hasHeader) {
        try {
            Sheet sheet = workbook.getSheetAt(0);
            List<T> list = new ArrayList<>();
            Field[] fields = getDeclaredFields(clazz);
            Map<String, Integer> map = new HashMap<>();
            //有表头就读成表头
            if (hasHeader) {
                Row row = sheet.getRow(0);
                for (int i = 0; i <= row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    map.put(getValue(cell, String.class), i);
                }
            }
            for (int i = hasHeader ? 1 : 0; i <= sheet.getLastRowNum(); i++) {
                T instance = clazz.newInstance();
                for (int j = 0; j < fields.length; j++) {
                    Field field = fields[j];
                    ReflectionUtils.makeAccessible(field);
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (null == annotation) {
                        //如果不是excel的字段则忽略
                        continue;
                    }
                    int index = annotation.index();
                    String value = annotation.value();
                    Integer headerIndex = map.get(value);
                    /*
                     * 1.所有值都是默认值
                     * 2.没有表头也没有给index
                     * 3.有表头但是value没有对应的表头，也没有index
                     */
                    if ((ExcelColumn.defaultValue.NULL_INDEX == index && ExcelColumn.defaultValue.NULL_VALUE.equals(value))
                            || (!hasHeader && ExcelColumn.defaultValue.NULL_INDEX == index)
                            || (hasHeader && null == headerIndex && ExcelColumn.defaultValue.NULL_INDEX == index)) {
                        continue;
                    }
                    Row row = sheet.getRow(i);
                    if (!hasHeader) {
                        //如果没表头了
                        Cell cell = row.getCell(index);
                        ReflectionUtils.setField(field, instance, getValue(cell, field.getType()));
                    } else {
                        //如果取不到表头的下标就尝试从index获取
                        if (null == headerIndex) {
                            headerIndex = index;
                        }
                        Cell cell = row.getCell(headerIndex);
                        ReflectionUtils.setField(field, instance, getValue(cell, field.getType()));
                    }
                }
                list.add(instance);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] fields;
        if (!CACHE_FIELDS.containsKey(clazz)) {
            fields = clazz.getDeclaredFields();
            CACHE_FIELDS.put(clazz, fields);
        } else {
            fields = CACHE_FIELDS.get(clazz);
        }
        return fields;
    }

    public static void clearCache() {
        CACHE_FIELDS.clear();
    }

    /**
     * 目前只对数字cell和文本cell进行处理
     *
     * @param cell  如果是空则返回null
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getValue(Cell cell, Class<T> clazz) {
        if (null == cell) {
            return null;
        }
        int cellType = cell.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_NUMERIC: {
                return getValue(cell.getNumericCellValue(), clazz);
            }
            case Cell.CELL_TYPE_STRING: {
                return getValue(cell.getStringCellValue(), clazz);
            }
            case Cell.CELL_TYPE_BLANK: {
                return null;
            }
            default: {
                return null;
            }
        }
    }

    private static <T> T getValue(double val, Class<T> clazz) {
        if (Long.class == clazz) {
            return (T) Long.valueOf(Double.valueOf(val).longValue());
        } else if (Integer.class == clazz) {
            return (T) Integer.valueOf(Double.valueOf(val).intValue());
        } else if (Short.class == clazz) {
            return (T) Short.valueOf(Double.valueOf(val).shortValue());
        } else if (Float.class == clazz) {
            return (T) Float.valueOf(Double.valueOf(val).floatValue());
        } else if (Double.class == clazz) {
            return (T) Double.valueOf(val);
        } else if (String.class == clazz) {
            return (T) NumberToTextConverter.toText(val);
        } else if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("unsupported primitive type");
        } else {
            return null;
        }
    }

    private static <T> T getValue(String val, Class<T> clazz) {
        if (Long.class == clazz) {
            return (T) Long.valueOf(val);
        } else if (Integer.class == clazz) {
            return (T) Integer.valueOf(val);
        } else if (Short.class == clazz) {
            return (T) Short.valueOf(val);
        } else if (Float.class == clazz) {
            return (T) Float.valueOf(val);
        } else if (Double.class == clazz) {
            return (T) Double.valueOf(val);
        } else if (String.class == clazz) {
            return (T) val;
        } else if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("unsupported primitive type");
        } else {
            return null;
        }
    }
}
