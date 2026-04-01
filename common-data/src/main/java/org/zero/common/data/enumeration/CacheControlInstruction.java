package org.zero.common.data.enumeration;

/**
 * HTTP {@code Cache-Control} 指令枚举，实现 {@link CacheStrategy} 接口。
 *
 * <p>每个枚举项对应一条标准缓存指令，通过 {@link #getCacheControl()} 返回指令字符串，
 * 可直接用于 HTTP 响应头 {@code Cache-Control} 的值。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/3
 */
public enum CacheControlInstruction implements CacheStrategy {
    /** 禁止缓存——请求和响应都不允许被任何缓存存储。 */
    NO_STORE {
        @Override
        public String getCacheControl() {
            return "no-store";
        }
    },
    /** 强制回源——缓存必须先向服务器验证后再使用已缓存的响应。 */
    NO_CACHE {
        @Override
        public String getCacheControl() {
            return "no-cache";
        }
    },
    /** 禁止转换——中间代理不得对响应体进行转码或压缩等修改。 */
    NO_TRANSFORM {
        @Override
        public String getCacheControl() {
            return "no-transform";
        }
    },
    /** 必须重新验证——缓存一旦过期，必须向服务器验证后才能继续使用。 */
    MUST_REVALIDATE {
        @Override
        public String getCacheControl() {
            return "must-revalidate";
        }
    },
    ;
}
