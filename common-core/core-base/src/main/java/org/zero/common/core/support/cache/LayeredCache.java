package org.zero.common.core.support.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/17
 */
public class LayeredCache<K, V> extends MultiCache<K, V> {
	protected LayeredCache(Collection<Cache<K, V>> caches) {
		super(caches);
	}

	@Override
	protected Collection<Cache<K, V>> selectCaches(K key) {
		return caches;
	}

	@SafeVarargs
	public static <K, V> LayeredCache<K, V> of(Cache<K, V>... caches) {
		return of(new ArrayList<>(Arrays.asList(caches)));
	}

	public static <K, V> LayeredCache<K, V> of(Collection<Cache<K, V>> caches) {
		return new LayeredCache<>(caches);
	}
}
