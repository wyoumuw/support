package com.youmu;

import com.youmu.utils.PageUtils;
import com.youmu.utils.StreamUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author YOUMU
 * @version V1.0
 * @since 2019-11-06 17:12
 */
public class PageUtilsTest {

    @Test
    public void example1() {
        List<Integer> list = IntStream.range(1, 100).mapToObj(Integer::valueOf).collect(Collectors.toList());
        List<Integer> result = PageUtils.pageFetchAll(1, page -> {
            int pageSize = 10;
            int offset = (page - 1) * pageSize;
            int end = offset + 10;
            if (end > list.size()) {
                return PageUtils.PageResult.of(list.subList(offset, list.size()), false);
            } else {
                return PageUtils.PageResult.of(list.subList(offset, end), true);
            }
        });
        Assert.assertEquals(list, result);
    }

    @Test
    public void example2() {
        List<Integer> list = IntStream.range(1, 100).boxed().collect(Collectors.toList());
        List<Integer> result = PageUtils.pageFetchAll(1, page -> {
            int pageSize = 10;
            int offset = (page - 1) * pageSize;
            int end = offset + 10;
            if (end > list.size()) {
                return null;
            } else {
                return PageUtils.PageResult.of(list.subList(offset, end), true);
            }
        });
        Assert.assertNotEquals(list, result);
    }
}
