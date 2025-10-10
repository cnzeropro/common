package org.zero.common.core.extension.slf4j.spi;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.spi.MDCAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义 MDC 适配器
 * <p>
 * alibaba ttl 建议使用 agent 方式：{@code -javaagent:path/to/transmittable-thread-local.jar}
 * <p>
 * 使用方式：建议在自己的项目中，放入 {@code org.slf4j.impl.StaticMDCBinder} 类，具体实现参考 {@code logback-classic} 包中的该类
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/17
 */
public class CustomMDCAdapter implements MDCAdapter {
	protected final ThreadLocal<Map<String, String>> context = TransmittableThreadLocal.withInitial(HashMap::new);

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

	protected Map<String, String> getContextMap() {
		Map<String, String> map = context.get();
		if (Objects.isNull(map)) {
			map = new HashMap<>();
			context.set(map);
		}
		return map;
	}
}
