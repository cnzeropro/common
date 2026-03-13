package org.zero.common.core.extension.slf4j.spi;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.spi.MDCAdapter;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * Custom MDC adapter backed by TransmittableThreadLocal.
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/17
 */
public class CustomMDCAdapter implements MDCAdapter {
	protected final ThreadLocal<Map<String, String>> context = TransmittableThreadLocal.withInitial(HashMap::new);
	protected final ThreadLocal<Map<String, Deque<String>>> dequeContext = TransmittableThreadLocal.withInitial(HashMap::new);

	@Override
	public void put(String key, String val) {
		this.getContextMap().put(key, val);
	}

	@Override
	public String get(String key) {
		return this.getContextMap().get(key);
	}

	@Override
	public void remove(String key) {
		this.getContextMap().remove(key);
	}

	@Override
	public void clear() {
		this.getContextMap().clear();
		this.getDequeContextMap().clear();
	}

	@Override
	public Map<String, String> getCopyOfContextMap() {
		Map<String, String> map = this.getContextMap();
		return new HashMap<>(map);
	}

	@Override
	public void setContextMap(Map<String, String> contextMap) {
		HashMap<String, String> map = new HashMap<>(contextMap);
		context.set(map);
	}

	@Override
	public void pushByKey(String key, String value) {
		this.getDequeContextMap().computeIfAbsent(key, ignored -> new LinkedList<>()).push(value);
	}

	@Override
	public String popByKey(String key) {
		Deque<String> deque = this.getDequeContextMap().get(key);
		if (Objects.isNull(deque) || deque.isEmpty()) {
			return null;
		}
		String value = deque.pop();
		if (deque.isEmpty()) {
			this.getDequeContextMap().remove(key);
		}
		return value;
	}

	@Override
	public Deque<String> getCopyOfDequeByKey(String key) {
		Deque<String> deque = this.getDequeContextMap().get(key);
		if (Objects.isNull(deque) || deque.isEmpty()) {
			return null;
		}
		return new LinkedList<>(deque);
	}

	@Override
	public void clearDequeByKey(String key) {
		this.getDequeContextMap().remove(key);
	}

	protected Map<String, String> getContextMap() {
		Map<String, String> map = context.get();
		if (Objects.isNull(map)) {
			map = new HashMap<>();
			context.set(map);
		}
		return map;
	}

	protected Map<String, Deque<String>> getDequeContextMap() {
		Map<String, Deque<String>> map = dequeContext.get();
		if (Objects.isNull(map)) {
			map = new HashMap<>();
			dequeContext.set(map);
		}
		return map;
	}
}
