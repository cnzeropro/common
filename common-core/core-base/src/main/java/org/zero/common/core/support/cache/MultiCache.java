package org.zero.common.core.support.cache;

import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/21
 */
@Log
public abstract class MultiCache<K, V> implements Cache<K, V> {
	protected final Collection<Cache<K, V>> caches;

	protected MultiCache(Collection<Cache<K, V>> caches) {
		this.caches = caches;
	}

	@Override
	public void set(K key, V value) {
		Collection<Cache<K, V>> selectedCaches = this.selectCaches(key);
		Collection<Cache<K, V>> puttedCaches = new ArrayList<>(selectedCaches.size());
		for (Cache<K, V> selectedCache : selectedCaches) {
			try {
				selectedCache.put(key, value);
			} catch (Exception e) {
				puttedCaches.forEach(cache -> cache.remove(key));
				throw e;
			}
			puttedCaches.add(selectedCache);
		}
	}

	@Override
	public V get(K key) {
		Collection<Cache<K, V>> selectedCaches = this.selectCaches(key);
		for (Cache<K, V> cache : selectedCaches) {
			V value = cache.get(key);
			if (Objects.nonNull(value)) {
				return value;
			}
		}
		return null;
	}

	@Override
	public Map<K, V> asMap() {
		Map<K, V> result = new HashMap<>();
		for (Cache<K, V> cache : caches) {
			Map<K, V> map = cache.asMap();
			result.putAll(map);
		}
		return Collections.unmodifiableMap(result);
	}

	@Override
	public boolean existKey(K key) {
		Collection<Cache<K, V>> selectedCaches = this.selectCaches(key);
		for (Cache<K, V> cache : selectedCaches) {
			if (cache.existKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void remove(K key) {
		Collection<Cache<K, V>> selectedCaches = this.selectCaches(key);
		for (Cache<K, V> cache : selectedCaches) {
			try {
				cache.remove(key);
			} catch (Exception e) {
				log.log(Level.FINE, String.format("Failed to remove key[%s] from cache[%s]", key, cache), e);
			}
		}
	}

	@Override
	public void removeAll() {
		for (Cache<K, V> cache : caches) {
			try {
				cache.removeAll();
			} catch (Exception e) {
				log.log(Level.FINE, String.format("Failed to remove all from cache[%s]", cache), e);
			}
		}
	}

	public void addCache(Cache<K, V> cache) {
		caches.add(cache);
	}

	public void addCaches(Cache<K, V>... caches) {
		this.addCaches(Arrays.asList(caches));
	}

	public void addCaches(Collection<Cache<K, V>> caches) {
		this.caches.addAll(caches);
	}

	public void removeCache(Cache<K, V> cache) {
		caches.remove(cache);
	}

	public void removeCaches(Cache<K, V>... caches) {
		this.removeCaches(Arrays.asList(caches));
	}

	public void removeCaches(Collection<Cache<K, V>> caches) {
		this.caches.removeAll(caches);
	}

	protected abstract Collection<Cache<K, V>> selectCaches(K key);
}
