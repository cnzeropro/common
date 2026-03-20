package org.zero.common.api.extra.loki.model.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.Collection;
import java.util.Map;

/**
 * Loki 日志流结果。
 * <p>
 * 对应 {@code resultType=streams} 的单个结果项，包含一组流标签和多条日志值。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class LokiStream extends LokiResult {
    /**
     * {@code stream} - 当前日志流的标签集合。
     */
    private Map<String, Object> stream;
    /**
     * {@code values} - 当前日志流下的日志值数组列表。
     */
    @Singular
    private Collection<Value> values;

    /**
     * Loki 日志值对象。
     * <p>
     * 对应日志流中的单条值数组，结构为
     * {@code [epochNano, logLine]} 或 {@code [epochNano, logLine, structuredMetadata]}。
     */
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode(callSuper = true)
    public static class Value extends LokiValue {
        /**
         * 使用时间戳和日志行构造日志值。
         *
         * @param epochNano 纳秒级 Unix 时间戳字符串
         * @param logLine   日志原文
         */
        public Value(String epochNano, String logLine) {
            this(epochNano, logLine, null);
        }

        /**
         * 使用时间戳、日志行和 structured metadata - 结构化元数据构造日志值。
         *
         * @param epochNano          纳秒级 Unix 时间戳字符串
         * @param logLine            日志原文
         * @param structuredMetadata 第三段结构化元数据
         */
        public Value(String epochNano, String logLine, Map<String, String> structuredMetadata) {
            super(structuredMetadata == null ? 2 : 3);
            this.add(epochNano);
            this.add(logLine);
            if (structuredMetadata != null) {
                this.add(structuredMetadata);
            }
        }

        /**
         * 获取第 1 段 {@code epochNano}。
         *
         * @return 纳秒级 Unix 时间戳字符串
         */
        public String getEpochNano() {
            return (String) this.get(0);
        }

        /**
         * 获取第 2 段 {@code logLine}。
         *
         * @return 日志原文
         */
        public String getLogLine() {
            return (String) this.get(1);
        }

        /**
         * 获取第 3 段 {@code structured metadata}。
         *
         * @return 结构化元数据；如果当前值只有两段则返回 {@code null}
         */
        @SuppressWarnings("unchecked")
        public Map<String, String> getStructuredMetadata() {
            if (this.size() <= 2) {
                return null;
            }
            return (Map<String, String>) this.get(2);
        }
    }
}
