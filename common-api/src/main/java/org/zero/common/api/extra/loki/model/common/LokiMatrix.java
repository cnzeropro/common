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
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class LokiMatrix extends LokiResult {
    private Map<String, Object> metric;
    @Singular
    private Collection<Value> values;

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode(callSuper = true)
    public static class Value extends LokiValue {
        public Value(Long epochSecond, String logLine) {
            super();
            this.add(epochSecond);
            this.add(logLine);
        }

        public Long getEpochSecond() {
            return (Long) this.get(0);
        }

        public String getLogLine() {
            return (String) this.get(1);
        }
    }
}
