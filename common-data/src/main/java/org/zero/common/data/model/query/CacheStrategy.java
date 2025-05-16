package org.zero.common.data.model.query;

/**
 * 缓存策略
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
public interface CacheStrategy {
    /**
     * 获取缓存指令
     * <p>
     * 如：no-cache, no-store, max-age=0, must-revalidate
     *
     * @return 缓存指令
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/Cache-Control">Cache-Control</a>
     */
    String getCacheControl();
}
