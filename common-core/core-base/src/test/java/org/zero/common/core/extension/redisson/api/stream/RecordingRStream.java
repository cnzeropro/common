package org.zero.common.core.extension.redisson.api.stream;

import org.redisson.api.AutoClaimResult;
import org.redisson.api.PendingEntry;
import org.redisson.api.PendingResult;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 记录 Stream 调用的轻量测试替身，避免为接口模拟引入额外依赖。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
final class RecordingRStream<K, V> implements InvocationHandler {
	private final RStream<K, V> proxy;
	private final Map<String, Integer> invocationCounts = new HashMap<>();
	private final List<StreamMessageId> acknowledgedIds = new ArrayList<>();
	private final List<StreamMessageId> fastClaimedIds = new ArrayList<>();
	private final List<StreamMessageId> removedIds = new ArrayList<>();

	private Map<StreamMessageId, Map<K, V>> readGroupResult = Collections.emptyMap();
	private PendingResult pendingInfo = new PendingResult(0, null, null, Collections.emptyMap());
	private List<PendingEntry> pendingEntries = Collections.emptyList();
	private Map<StreamMessageId, Map<K, V>> pendingRangeResult = Collections.emptyMap();
	private AutoClaimResult<K, V> autoClaimResult = new AutoClaimResult<>(
			new StreamMessageId(0, 0),
			Collections.emptyMap(),
			Collections.emptyList()
	);
	private List<StreamMessageId> fastClaimResult = Collections.emptyList();
	private int ackWithArgsCount;
	private String lastAckGroupName;
	private String lastAutoClaimGroupName;
	private String lastAutoClaimConsumerName;
	private long lastAutoClaimIdleTime;
	private TimeUnit lastAutoClaimIdleTimeUnit;
	private StreamMessageId lastAutoClaimStartId;
	private int lastAutoClaimCount;
	private String lastFastClaimGroupName;
	private String lastFastClaimConsumerName;
	private long lastFastClaimIdleTime;
	private TimeUnit lastFastClaimIdleTimeUnit;
	private int addCount;
	private Object lastAddArgs;

	@SuppressWarnings("unchecked")
	private RecordingRStream() {
		this.proxy = (RStream<K, V>) Proxy.newProxyInstance(
				RStream.class.getClassLoader(),
				new Class<?>[]{RStream.class},
				this
		);
	}

	static <K, V> RecordingRStream<K, V> create() {
		return new RecordingRStream<>();
	}

	RStream<K, V> proxy() {
		return proxy;
	}

	RecordingRStream<K, V> readGroupResult(Map<StreamMessageId, Map<K, V>> readGroupResult) {
		this.readGroupResult = readGroupResult;
		return this;
	}

	RecordingRStream<K, V> pendingInfo(PendingResult pendingInfo) {
		this.pendingInfo = pendingInfo;
		return this;
	}

	RecordingRStream<K, V> pendingEntries(List<PendingEntry> pendingEntries) {
		this.pendingEntries = pendingEntries;
		return this;
	}

	RecordingRStream<K, V> pendingRangeResult(Map<StreamMessageId, Map<K, V>> pendingRangeResult) {
		this.pendingRangeResult = pendingRangeResult;
		return this;
	}

	RecordingRStream<K, V> autoClaimResult(AutoClaimResult<K, V> autoClaimResult) {
		this.autoClaimResult = autoClaimResult;
		return this;
	}

	RecordingRStream<K, V> fastClaimResult(List<StreamMessageId> fastClaimResult) {
		this.fastClaimResult = fastClaimResult;
		return this;
	}

	List<StreamMessageId> getAcknowledgedIds() {
		return acknowledgedIds;
	}

	List<StreamMessageId> getFastClaimedIds() {
		return fastClaimedIds;
	}

	List<StreamMessageId> getRemovedIds() {
		return removedIds;
	}

	int getAckWithArgsCount() {
		return ackWithArgsCount;
	}

	int getAddCount() {
		return addCount;
	}

	Object getLastAddArgs() {
		return lastAddArgs;
	}

	String getLastAckGroupName() {
		return lastAckGroupName;
	}

	String getLastAutoClaimGroupName() {
		return lastAutoClaimGroupName;
	}

	String getLastAutoClaimConsumerName() {
		return lastAutoClaimConsumerName;
	}

	long getLastAutoClaimIdleTime() {
		return lastAutoClaimIdleTime;
	}

	TimeUnit getLastAutoClaimIdleTimeUnit() {
		return lastAutoClaimIdleTimeUnit;
	}

	StreamMessageId getLastAutoClaimStartId() {
		return lastAutoClaimStartId;
	}

	int getLastAutoClaimCount() {
		return lastAutoClaimCount;
	}

	String getLastFastClaimGroupName() {
		return lastFastClaimGroupName;
	}

	String getLastFastClaimConsumerName() {
		return lastFastClaimConsumerName;
	}

	long getLastFastClaimIdleTime() {
		return lastFastClaimIdleTime;
	}

	TimeUnit getLastFastClaimIdleTimeUnit() {
		return lastFastClaimIdleTimeUnit;
	}

	int getInvocationCount(String methodName) {
		Integer count = invocationCounts.get(methodName);
		return count == null ? 0 : count;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		if (method.getDeclaringClass() == Object.class) {
			return this.invokeObjectMethod(method, args);
		}

		String methodName = method.getName();
		invocationCounts.put(methodName, this.getInvocationCount(methodName) + 1);
		if ("readGroup".equals(methodName)) {
			return readGroupResult;
		}
		if ("add".equals(methodName)) {
			return this.add(method, args);
		}
		if ("ack".equals(methodName)) {
			return this.ack(args);
		}
		if ("remove".equals(methodName)) {
			return this.remove(args);
		}
		if ("getPendingInfo".equals(methodName)) {
			return pendingInfo;
		}
		if ("listPending".equals(methodName)) {
			return pendingEntries;
		}
		if ("pendingRange".equals(methodName)) {
			return pendingRangeResult;
		}
		if ("autoClaim".equals(methodName)) {
			return this.autoClaim(args);
		}
		if ("fastClaim".equals(methodName)) {
			return this.fastClaim(args);
		}
		throw new UnsupportedOperationException("Unsupported RStream method: " + methodName);
	}

	private Object invokeObjectMethod(Method method, Object[] args) {
		if ("toString".equals(method.getName())) {
			return "RecordingRStream";
		}
		if ("hashCode".equals(method.getName())) {
			return System.identityHashCode(proxy);
		}
		if ("equals".equals(method.getName())) {
			return proxy == args[0];
		}
		throw new UnsupportedOperationException("Unsupported Object method: " + method.getName());
	}

	private Object add(Method method, Object[] args) {
		addCount++;
		lastAddArgs = args == null || args.length == 0 ? null : args[args.length - 1];
		if (Void.TYPE == method.getReturnType()) {
			return null;
		}
		return new StreamMessageId(100, 0);
	}

	private Object ack(Object[] args) {
		if (args != null && args.length == 2 && args[0] instanceof String) {
			lastAckGroupName = (String) args[0];
			StreamMessageId[] ids = (StreamMessageId[]) args[1];
			acknowledgedIds.addAll(Arrays.asList(ids));
			return Long.valueOf(ids.length);
		}
		ackWithArgsCount++;
		return Collections.emptyMap();
	}

	private Object remove(Object[] args) {
		if (args != null && args.length == 1 && args[0] instanceof StreamMessageId[]) {
			StreamMessageId[] ids = (StreamMessageId[]) args[0];
			removedIds.addAll(Arrays.asList(ids));
			return Long.valueOf(ids.length);
		}
		return Collections.emptyMap();
	}

	private Object autoClaim(Object[] args) {
		lastAutoClaimGroupName = (String) args[0];
		lastAutoClaimConsumerName = (String) args[1];
		lastAutoClaimIdleTime = ((Long) args[2]).longValue();
		lastAutoClaimIdleTimeUnit = (TimeUnit) args[3];
		lastAutoClaimStartId = (StreamMessageId) args[4];
		lastAutoClaimCount = ((Integer) args[5]).intValue();
		return autoClaimResult;
	}

	private Object fastClaim(Object[] args) {
		lastFastClaimGroupName = (String) args[0];
		lastFastClaimConsumerName = (String) args[1];
		lastFastClaimIdleTime = ((Long) args[2]).longValue();
		lastFastClaimIdleTimeUnit = (TimeUnit) args[3];
		StreamMessageId[] ids = (StreamMessageId[]) args[4];
		fastClaimedIds.addAll(Arrays.asList(ids));
		return fastClaimResult;
	}
}
