package org.zero.common.data.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.zero.common.data.model.query.PageQO.DEFAULT_NUMBER;
import static org.zero.common.data.model.query.PageQO.DEFAULT_SIZE;

/**
 * 通用分页视图对象，用于封装分页查询结果。
 *
 * @param <T> 数据列表元素类型
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/02
 */
@Data
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> implements Serializable {
	private static final long serialVersionUID = 4804624538834728712L;

	/**
	 * 当前页码，从 1 开始。
	 */
	@Builder.Default
	protected long current = DEFAULT_NUMBER;

	/**
	 * 每页条数。
	 */
	@Builder.Default
	protected long size = DEFAULT_SIZE;

	/**
	 * 数据总数。
	 */
	@Builder.Default
	protected long count = 0L;

	/**
	 * 数据列表。
	 */
	@With
	@Builder.Default
	protected Collection<T> contents = Collections.emptyList();

	/**
	 * 计算总页数。
	 *
	 * @return 总页数
	 */
	public long getTotal() {
		if (count <= 0L || size <= 0L) {
			return 0L;
		}
		long total = count / size;
		return count % size == 0L ? total : total + 1L;
	}

	/* ******************************************************* builder ******************************************************* */

	public static <T> PageVO<T> of() {
		return of(DEFAULT_NUMBER);
	}

	public static <T> PageVO<T> of(long current) {
		return of(current, DEFAULT_SIZE);
	}

	public static <T> PageVO<T> of(long current, long size) {
		return of(current, size, 0L);
	}

	public static <T> PageVO<T> of(long current, long size, long count) {
		return of(current, size, count, Collections.emptyList());
	}

	public static <T> PageVO<T> of(long current, long size, long count, Collection<T> contents) {
		return new PageVO<>(current, size, count, contents);
	}

	protected PageVO(long current, long size, long count) {
		this.current = current;
		this.size = size;
		this.count = count;
		this.contents = Collections.emptyList();
	}

	/* ******************************************************* converter ******************************************************* */

	public <R> PageVO<R> convert(Function<? super T, ? extends R> mapper) {
		List<R> data = this.getContents().stream().map(mapper).collect(Collectors.toList());
		return this.convert(data);
	}

	public <R> PageVO<R> convertNew(Function<? super T, ? extends R> mapper) {
		List<R> data = this.getContents().stream().map(mapper).collect(Collectors.toList());
		return this.convertNew(data);
	}

	@SuppressWarnings("unchecked")
	public <R> PageVO<R> convert(Collection<R> data) {
		return ((PageVO<R>) this).setContents(data);
	}

	@SuppressWarnings("unchecked")
	public <R> PageVO<R> convertNew(Collection<R> data) {
		return ((PageVO<R>) this).withContents(data);
	}
}
