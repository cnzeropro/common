package org.zero.common.data.model.view;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.stream.Collectors;

import static org.zero.common.data.model.query.PageQO.DEFAULT_NUMBER;
import static org.zero.common.data.model.query.PageQO.DEFAULT_SIZE;

/**
 * 带参数修正能力的分页视图对象。
 *
 * @param <T> 数据列表元素类型
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/08/18 9:07
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmartPageVO<T> extends PageVO<T> {
	private static final long serialVersionUID = 8463126863903128798L;

	/* ******************************************************* setter ******************************************************* */

	/**
	 * 设置并修正当前页码。
	 */
	@Override
	public SmartPageVO<T> setCurrent(long current) {
		long total = getTotal();
		if (total > 0L && total < current) {
			current = total;
		}
		if (current < DEFAULT_NUMBER) {
			current = DEFAULT_NUMBER;
		}
		this.current = current;
		return this;
	}

	/**
	 * 设置并修正每页条数。
	 */
	@Override
	public SmartPageVO<T> setSize(long size) {
		if (size <= 0L) {
			size = DEFAULT_SIZE;
		}
		this.size = size;
		return setCurrent(this.current);
	}

	/**
	 * 设置并修正数据总数。
	 */
	@Override
	public SmartPageVO<T> setCount(long count) {
		this.count = count;
		return setCurrent(this.current);
	}

	/**
	 * 设置数据列表。
	 */
	@Override
	public SmartPageVO<T> setContents(Collection<T> contents) {
		this.contents = contents;
		return this;
	}

	/* ******************************************************* builder ******************************************************* */

	public static <T> SmartPageVO<T> of() {
		return of(DEFAULT_NUMBER);
	}

	public static <T> SmartPageVO<T> of(long current) {
		return of(current, DEFAULT_SIZE);
	}

	public static <T> SmartPageVO<T> of(long current, long size) {
		return of(current, size, 0L);
	}

	public static <T> SmartPageVO<T> of(long current, long size, long count) {
		return of(current, size, count, Collections.emptyList());
	}

	public static <T> SmartPageVO<T> of(long current, long size, long count, Collection<T> contents) {
		return new SmartPageVO<>(current, size, count, contents);
	}

	protected SmartPageVO(long current, long size, long count, Collection<T> contents) {
		this();
		setCurrent(current);
		setSize(size);
		setCount(count);
		setContents(contents);
	}

	/* ******************************************************* with ******************************************************* */

	public SmartPageVO<T> withCurrent(long current) {
		return copy().setCurrent(current);
	}

	public SmartPageVO<T> withSize(long size) {
		return copy().setSize(size);
	}

	public SmartPageVO<T> withCount(long count) {
		return copy().setCount(count);
	}

	@Override
	public SmartPageVO<T> withContents(Collection<T> contents) {
		return copy().setContents(contents);
	}

	public SmartPageVO<T> withCurrentBy(LongUnaryOperator operator) {
		return withCurrent(operator.applyAsLong(this.current));
	}

	public SmartPageVO<T> withSizeBy(LongUnaryOperator operator) {
		return withSize(operator.applyAsLong(this.size));
	}

	public SmartPageVO<T> withCountBy(LongUnaryOperator operator) {
		return withCount(operator.applyAsLong(this.count));
	}

	public SmartPageVO<T> withContentsBy(Function<? super Collection<T>, ? extends Collection<T>> operator) {
		return withContents(operator.apply(this.contents));
	}

	public SmartPageVO<T> copy() {
		return SmartPageVO.of(current, size, count, contents);
	}

	/* ******************************************************* converter ******************************************************* */

	@Override
	public <R> SmartPageVO<R> convert(Function<? super T, ? extends R> mapper) {
		List<R> data = this.getContents().stream().map(mapper).collect(Collectors.toList());
		return this.convert(data);
	}

	@Override
	public <R> SmartPageVO<R> convertNew(Function<? super T, ? extends R> mapper) {
		List<R> data = this.getContents().stream().map(mapper).collect(Collectors.toList());
		return this.convertNew(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> SmartPageVO<R> convert(Collection<R> data) {
		return ((SmartPageVO<R>) this).setContents(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> SmartPageVO<R> convertNew(Collection<R> data) {
		return ((SmartPageVO<R>) this).withContents(data);
	}
}
