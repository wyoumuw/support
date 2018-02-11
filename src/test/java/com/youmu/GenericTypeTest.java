package com.youmu;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2017/11/15
 */
public class GenericTypeTest {

    @Test
    public void superTest() {
        List<Number> list = Lists.newArrayList();
        Map<UtilsTest.HttpCode, String> map = new EnumMap<UtilsTest.HttpCode, String>(
                UtilsTest.HttpCode.class);
        map.put(UtilsTest.HttpCode.CODE_200, UtilsTest.HttpCode.CODE_200.getDesc());
        System.out.println(map);

    }

    static interface Interface {
    }
}
