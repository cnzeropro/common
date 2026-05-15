package org.zero.common.core.extension.java;

/**
 * 生命周期回调接口。
 * <p>
 * 适用于需要显式初始化和释放资源的组件。调用顺序通常为先 {@link #initialize()} 后 {@link #destroy()}，
 * 是否支持重复调用、失败重试或并发调用由具体实现自行保证。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
public interface LifeCycle {
	/**
	 * 初始化组件。
	 * <p>
	 * 可在此阶段完成资源分配、连接建立、配置校验等启动前准备。
	 *
	 * @throws Exception 初始化失败时报错
	 */
	void initialize() throws Exception;

	/**
	 * 销毁组件。
	 * <p>
	 * 可在此阶段关闭连接、释放缓存、停止后台任务等资源。
	 *
	 * @throws Exception 销毁失败时报错
	 */
	void destroy() throws Exception;
}
