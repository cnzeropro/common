package org.zero.common.data.model.transfer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.zero.common.data.constant.CommonConstant.DEFAULT_PAGE_SIZE;

/**
 * 带修正的分页对象（新建、更新时自动修正相关参数，但是影响性能）
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/8/18 9:07
 */
@Data
@With
@WithBy
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SmartPageDTO<T> implements Serializable {
    private static final long serialVersionUID = 8463126863903128798L;

    /**
     * 当前页码
     */
    protected long current = 1L;
    /**
     * 每页数目
     */
    protected long size = DEFAULT_PAGE_SIZE;
    /**
     * 总页数
     */
    protected long total = 0L;
    /**
     * 数据总数
     */
    protected long recordCount = 0L;

    /**
     * 数据列表
     */
    protected Collection<T> records = Collections.emptyList();

    /* ******************************************************* setter ******************************************************* */

    /**
     * 设置并修正当前页码
     */
    public SmartPageDTO<T> setCurrent(long current) {
        if (total > 0 && total < current) {
            current = total;
        }
        if (current < 1) {
            current = 1;
        }
        this.current = current;
        return this;
    }

    /**
     * 设置并修正每页条数
     */
    public SmartPageDTO<T> setSize(long size) {
        if (size <= 0) {
            size = DEFAULT_PAGE_SIZE;
        }
        this.size = size;
        return setTotal(total);
    }

    /**
     * 设置并修正总页数
     */
    public SmartPageDTO<T> setTotal(long total) {
        if (recordCount > 0 && size > 0) {
            total = recordCount / size;
            if (recordCount % size != 0) {
                total++;
            }
        }
        this.total = total;
        return setCurrent(current);
    }

    /**
     * 设置记录条数
     */
    public SmartPageDTO<T> setRecordCount(long recordCount) {
        this.recordCount = recordCount;
        return setTotal(total);
    }

    /* ******************************************************* builder ******************************************************* */

    public static <T> SmartPageDTO<T> of() {
        return new SmartPageDTO<>();
    }

    public static <T> SmartPageDTO<T> of(long current) {
        return of(current, DEFAULT_PAGE_SIZE);
    }

    public static <T> SmartPageDTO<T> of(long current, long size) {
        return of(current, size, 0L);
    }

    public static <T> SmartPageDTO<T> of(long current, long size, long recordCount) {
        return new SmartPageDTO<>(current, size, recordCount);
    }

    protected SmartPageDTO(long current, long size, long recordCount) {
        this();
        setCurrent(current);
        setSize(size);
        setRecordCount(recordCount);
    }

    /* ******************************************************* converter ******************************************************* */

    public <R> SmartPageDTO<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> data = this.getRecords().stream().map(mapper).collect(Collectors.toList());
        return this.convert(data);
    }

    public <R> SmartPageDTO<R> convertNew(Function<? super T, ? extends R> mapper) {
        List<R> data = this.getRecords().stream().map(mapper).collect(Collectors.toList());
        return this.convertNew(data);
    }

    @SuppressWarnings("unchecked")
    public <R> SmartPageDTO<R> convert(Collection<R> data) {
        return ((SmartPageDTO<R>) this).setRecords(data);
    }

    @SuppressWarnings("unchecked")
    public <R> SmartPageDTO<R> convertNew(Collection<R> data) {
        return ((SmartPageDTO<R>) this).withRecords(data);
    }
}
