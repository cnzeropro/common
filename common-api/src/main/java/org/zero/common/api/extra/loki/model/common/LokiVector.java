package org.zero.common.api.extra.loki.model.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Loki 瞬时指标结果。
 * <p>
 * 对应 {@code resultType=vector} 的单个结果项，包含指标标签和单个时间点的指标值。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class LokiVector extends LokiResult {
    /**
     * {@code metric} - 当前瞬时指标结果对应的标签集合。
     */
    private Map<String, Object> metric;
    /**
     * {@code value} - 单个时间点的指标值数组。
     */
    private Value value;

    /**
     * Loki 瞬时指标值对象。
     * <p>
     * 对应向量结果中的单个值数组，结构为 {@code [epochSecond, metricValue]}。
     */
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode(callSuper = true)
    public static class Value extends LokiValue {
        /**
         * 使用时间戳和指标值构造瞬时指标值。
         *
         * @param epochSecond Unix 秒级时间戳
         * @param metricValue 指标值字符串
         */
        public Value(Long epochSecond, String metricValue) {
            super();
            this.add(epochSecond);
            this.add(metricValue);
        }

        /**
         * 获取第 1 段 {@code epochSecond}。
         *
         * @return Unix 秒级时间戳
         */
        public Long getEpochSecond() {
            return (Long) this.get(0);
        }

        /**
         * 获取第 2 段 {@code metricValue}。
         *
         * @return 指标值字符串
         */
        public String getMetricValue() {
            return (String) this.get(1);
        }
    }
}
