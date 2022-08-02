package com.youmu;

import com.youmu.utils.JSONUtils;
import com.youmu.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JUnitUtils {

    public static <T> List<ComparedResultModel> compareNoSort(List<T> expect, List<T> actual, Comparator<T> comparator) {
        if (null == expect) {
            expect = new ArrayList<>();
        }
        if (null == actual) {
            actual = new ArrayList<>();
        }
        List<ComparedResultModel> results = new ArrayList<>(expect.size() + actual.size());
        expect = expect.stream().sorted(comparator).collect(Collectors.toList());
        actual = actual.stream().sorted(comparator).collect(Collectors.toList());
        int expectIndex = 0, actualIndex = 0;
        while (expectIndex < expect.size() || actualIndex < actual.size()) {
            ComparedResultModel resultModel;
            if (expectIndex < expect.size() && actualIndex < actual.size()) {
                int compare = comparator.compare(expect.get(expectIndex), actual.get(actualIndex));
                if (compare < 0) {
                    resultModel = createResultModel(expect.get(expectIndex), null);
                    expectIndex++;
                } else if (compare > 0) {
                    resultModel = createResultModel(null, actual.get(actualIndex));
                    actualIndex++;
                } else {
                    resultModel = createResultModel(expect.get(expectIndex), actual.get(actualIndex));
                    expectIndex++;
                    actualIndex++;
                }
            } else if (expectIndex < expect.size()) {
                resultModel = createResultModel(expect.get(expectIndex++), null);
            } else {
                resultModel = createResultModel(null, actual.get(actualIndex++));
            }
            results.add(resultModel);
        }
        return results;
    }


    //
    private static ComparedResultModel createResultModel(Object expect, Object actual) {
        Class clazz = expect == null ? actual.getClass() : expect.getClass();
        try {
            ComparedResultModel resultModel = new ComparedResultModel();
            resultModel.setActual(actual);
            resultModel.setExpect(expect);
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
            ;
            List<ComparedPropertyModel> properties = new ArrayList<>();
            boolean anyDiff = false;
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                ComparedPropertyModel comparedPropertyModel = new ComparedPropertyModel();
                comparedPropertyModel.setName(propertyDescriptor.getName());
                Method readMethod = propertyDescriptor.getReadMethod();
                Object actualPropertyValue = null;
                Object expectPropertyValue = null;
                if (null != actual) {
                    actualPropertyValue = readMethod.invoke(actual);
                    comparedPropertyModel.setActual(actualPropertyValue);
                }
                if (null != expect) {
                    expectPropertyValue = readMethod.invoke(expect);
                    comparedPropertyModel.setExpect(expectPropertyValue);
                }
                comparedPropertyModel.setDiff(!Objects.equals(expectPropertyValue, actualPropertyValue));
                if (comparedPropertyModel.getDiff()) {
                    anyDiff = true;
                }
                properties.add(comparedPropertyModel);
            }
            resultModel.setProperties(properties);
            resultModel.setDiff(anyDiff);
            return resultModel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//
//    @Data
//    public static class ComparatorWrapper {
//        private Object obj;
//
//        private
//    }

    @Data
    public static class ComparedResultModel {
        private Object expect;

        private Object actual;

        private List<ComparedPropertyModel> properties;

        private Boolean diff;
    }

    @Data
    public static class ComparedPropertyModel {
        private String name;

        private Object expect;

        private Object actual;

        private Boolean diff = false;
    }


    /***********************************************/
    @Data
    @Accessors(chain = true)
    class A {
        private String x;
        private Integer y;
        private Integer b;
        private Integer id;

        public A copy() {
            A a = new A();
            a.setX(this.getX());
            a.setY(this.getY());
            a.setB(this.getB());
            a.setId(this.getId());
            return a;
        }
    }

    @Test
    public void Test() throws Exception {
        List<A> list1 = new ArrayList<>();
        List<A> list2 = new ArrayList<>();
        A a1 = randomA();
        A a2 = randomA();
        A a3 = randomA();
        A a4 = randomA();
        A a5 = randomA();
        A a6 = randomA();
        list1.add(a1);
        list1.add(a2);
        list1.add(a3);
        list1.add(a4);
        list1.add(a5);

        list2.add(a1.copy().setX(StringUtils.generateNonceStr(5)).setB(RandomUtils.nextInt()).setY(RandomUtils.nextInt()));
        list2.add(a2);
        list2.add(a6);
        list2.add(a3.copy().setX(StringUtils.generateNonceStr(5)).setB(RandomUtils.nextInt()).setY(RandomUtils.nextInt()));
        list2.add(a4.copy().setX(StringUtils.generateNonceStr(5)).setB(RandomUtils.nextInt()).setY(RandomUtils.nextInt()));
        list2.add(a5.copy().setX(StringUtils.generateNonceStr(5)).setB(RandomUtils.nextInt()).setY(RandomUtils.nextInt()));
        List<ComparedResultModel> comparedResultModels = compareNoSort(list1, list2, Comparator.comparing(A::getId));
        System.out.println(JSONUtils.serialize(comparedResultModels));

    }

    static int id = 1;

    private A randomA() {
        A a = new A();
        a.setX(StringUtils.generateNonceStr(5));
        a.setB(RandomUtils.nextInt());
        a.setY(RandomUtils.nextInt());
        a.setId(id++);
        return a;
    }
}
