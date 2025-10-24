package org.zero.common.core.extension.common.data.model.util;

import org.zero.common.data.model.util.Ordered;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/22
 */
public class OrderComparator implements Comparator<Ordered> {
	public static final OrderComparator INSTANCE = new OrderComparator();

	@Override
	public int compare(Ordered o1, Ordered o2) {
		return Integer.compare(o1.order(), o2.order());
	}

	public static <T extends Ordered> void sort(List<T> list) {
		if (list.size() > 1) {
			list.sort(INSTANCE);
		}
	}

	public static <T extends Ordered> void sort(T[] array) {
		if (array.length > 1) {
			Arrays.sort(array, INSTANCE);
		}
	}
}
