package com.youmu.utils;

import java.io.Serializable;

/**
 * 条件查询基类，如Entity需进行分页查询，需定义一个EntityQueryModel继承这个基类.
 */
public class QueryModel<T extends QueryModel> implements Serializable {

    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    /**
     * @Description 序列化id
     */
    private static final long serialVersionUID = 1L;

    private Integer pageNum = DEFAULT_PAGE_NUM;
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    private String orderBy; // 排序字段名称
    private Boolean isAsc = true; // 排序时是否正序，默认正序
    private Boolean countSql = true; // 是否进行count计算，false时也会使用limit
    private Boolean pageSizeZero = true; // true时，若pageSize=0，则不count和limit，输出所有结果
    private Boolean reasonable; // 是否合理化分页，默认情况也会合理化，例pageNum大于实际最大值时返回实际最大值
    private String[] properties; // 限制返回列名称列表

    /**
     * @Description 设置页码和每页大小，不符合的使用默认值
     * @param pageNumParam 页码
     * @param pageSizeParam 每页大小
     */
    public T setPageNumAndSize(Integer pageNumParam, Integer pageSizeParam) {
        // 页码校验
        if (null != pageNumParam && pageNumParam > 0) {
            this.pageNum = pageNumParam;
        } else {
            this.pageNum = DEFAULT_PAGE_NUM;
        }
        // 每页大小校验
        if (null != pageSizeParam && pageSizeParam >= 0) {
            this.pageSize = pageSizeParam;
        } else {
            this.pageSize = DEFAULT_PAGE_SIZE;
        }
        return (T) this;
    }

    /**
     * @Description 检测页码和每页大小，若异常则使用默认值
     * @author linb 2017年11月9日 下午5:23:36
     */
    public T checkPageNumAndSize() {
        if (null == this.pageNum || this.pageNum <= 0) {
            this.pageNum = DEFAULT_PAGE_NUM;
        }
        if (null == this.pageSize || this.pageSize < 0) {
            // pageSize为0时表示不分页
            this.pageSize = DEFAULT_PAGE_SIZE;
        }
        return (T) this;
    }

    /**
     * 获取页号
     * @return 页号
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * 设置页号
     * 请不要直接调用此方法而是选择{@link QueryModel#setPageNumAndSize(Integer, Integer)}
     * @param pageNum 页号
     */
    @SuppressWarnings("unchecked")
    public T setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return (T) this;
    }

    /**
     * 获取页大小
     * @return 页大小
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * 设置页大小
     * 请不要直接调用此方法而是选择{@link QueryModel#setPageNumAndSize(Integer, Integer)}
     * @param pageSize 页大小
     */
    @SuppressWarnings("unchecked")
    public T setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return (T) this;
    }

    /**
     * 获取排序字段
     * @return 排序字段
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置排序字段
     * @param orderBy 设置排序字段
     */
    public T setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return (T) this;
    }

    /**
     * @return 返回 isAsc 属性
     */
    public Boolean isAsc() {
        return isAsc;
    }

    /**
     * @param isAsc 设置 isAsc 属性
     */
    public T setAsc(Boolean isAsc) {
        this.isAsc = isAsc;
        return (T) this;
    }

    /**
     * 获取 count sql
     * @return count sql
     */
    public Boolean getCountSql() {
        return countSql;
    }

    /**
     * 设置 count sql
     * @param countSql count sql
     */
    public T setCountSql(Boolean countSql) {
        this.countSql = countSql;
        return (T) this;
    }

    /**
     * 获取 page size zero
     * @return page size zero
     */
    public Boolean getPageSizeZero() {
        return pageSizeZero;
    }

    /**
     * 设置 page size zero
     * @param pageSizeZero page size zero
     */
    public T setPageSizeZero(Boolean pageSizeZero) {
        this.pageSizeZero = pageSizeZero;
        return (T) this;
    }

    /**
     * 获取是否进行命理计算设置
     * @return true如设置了进行合理化计算，否则false
     */
    public Boolean getReasonable() {
        return reasonable;
    }

    /**
     * 设置是否进行合理化计算
     * @param reasonable 合理计算布尔值
     */
    public T setReasonable(Boolean reasonable) {
        this.reasonable = reasonable;
        return (T) this;
    }

    /**
     * @return 返回 properties 属性
     */
    public String[] getProperties() {
        return properties;
    }

    /**
     * @param properties 设置 properties 属性
     */
    public T setProperties(String... properties) {
        this.properties = properties;
        return (T) this;
    }

    /**
     * 全查不分页
     * @return
     */
    public T selectAll() {
        this.pageSize = 0;
        return (T) this;
    }
}
