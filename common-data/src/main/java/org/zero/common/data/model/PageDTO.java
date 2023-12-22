package org.zero.common.data.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 带修正的分页对象（新建、更新时自动修正相关参数，但是影响性能）
 *
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/8/18 9:07
 */
@Data
@Accessors(chain = true)
public class PageDTO<T> implements Serializable {
    private static final long serialVersionUID = 8463126863903128798L;

    /**
     * 默认每页数目：10
     */
    public static final String DEFAULT_PAGE_SIZE_STR = "10";
    // public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_SIZE = Integer.parseInt(DEFAULT_PAGE_SIZE_STR);

    /**
     * 当前页码
     */
    private long currentPage = 1L;
    /**
     * 每页数目
     */
    private long pageSize = DEFAULT_PAGE_SIZE;
    /**
     * 总页码
     */
    private long totalPage = 0L;
    /**
     * 记录总数
     */
    private long recordCount = 0L;

    /**
     * 数据对象列表
     */
    private List<T> records = Collections.emptyList();

    private PageDTO() {
    }

    private PageDTO(long currentPage, long pageSize) {
        setCurrentPage(currentPage);
        setPageSize(pageSize);
    }

    private PageDTO(long currentPage, long pageSize, long recordCount) {
        this(currentPage, pageSize);
        setRecordCount(recordCount);
    }

    /**
     * 设置并修正当前页码
     */
    public PageDTO<T> setCurrentPage(long currentPage) {
        if (totalPage > 0 && totalPage < currentPage) {
            currentPage = totalPage;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        this.currentPage = currentPage;
        return this;
    }

    /**
     * 设置并修正每页条数
     */
    public PageDTO<T> setPageSize(long pageSize) {
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.pageSize = pageSize;
        return setTotalPage(totalPage);
    }

    /**
     * 设置并修正总页数
     */
    public PageDTO<T> setTotalPage(long totalPage) {
        if (recordCount > 0 && pageSize > 0) {
            totalPage = recordCount / pageSize;
            if (recordCount % pageSize != 0) {
                totalPage++;
            }
        }
        this.totalPage = totalPage;
        return setCurrentPage(currentPage);
    }

    /**
     * 设置记录条数
     */
    public PageDTO<T> setRecordCount(long recordCount) {
        this.recordCount = recordCount;
        return setTotalPage(totalPage);
    }

    @SuppressWarnings("unchecked")
    public <R> PageDTO<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> data = this.getRecords().stream().map(mapper).collect(Collectors.toList());
        return ((PageDTO<R>) this).setRecords(data);
    }

    public static <T> PageDTO<T> of() {
        return new PageDTO<>();
    }

    public static <T> PageDTO<T> of(long currentPage) {
        return new PageDTO<>(currentPage, DEFAULT_PAGE_SIZE);
    }

    public static <T> PageDTO<T> of(long currentPage, long pageSize) {
        return new PageDTO<>(currentPage, pageSize);
    }

    public static <T> PageDTO<T> of(long currentPage, long pageSize, long recordCount) {
        return new PageDTO<>(currentPage, pageSize, recordCount);
    }
}
