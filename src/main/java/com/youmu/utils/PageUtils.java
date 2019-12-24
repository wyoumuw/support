package com.youmu.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 分页处理相关的工具
 *
 * @author YOUMU
 * @version V1.0
 * @since 2019-10-10 16:49
 */
public final class PageUtils {
    private PageUtils() {
    }

    public static <T> List<T> pageFetchAll(int firstPage, PageExecutor<T> executor) {
        List<T> result = new LinkedList<>();
        if (null != executor) {
            while (true) {
                PageResult<T> pageResult = executor.page(firstPage++);
                List<T> list;
                if (null == pageResult || null == (list = pageResult.getList())) {
                    break;
                }
                result.addAll(list);
                if (!pageResult.getHasNextPage()) {
                    break;
                }
            }
        }
        return result;
    }

    public interface PageExecutor<T> {
        PageResult<T> page(int page);
    }

    public static class PageResult<T> {
        private List<T> list;
        private boolean hasNextPage;

        private PageResult(List<T> list, boolean hasNextPage) {
            this.list = list;
            this.hasNextPage = hasNextPage;
        }

        public PageResult() {
        }

        public static <T> PageResult<T> of(List<T> list, boolean hasNextPage) {
            return new PageResult<>(list, hasNextPage);
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public boolean getHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }
    }
}
