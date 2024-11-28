package org.zero.common.api.extra.loki.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Matrix implements Serializable {
    private Map<String, Object> metric;
    @Singular
    private List<Value> values;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class Value extends org.zero.common.api.extra.loki.model.common.Value {
        public Value() {
            super();
        }

        public Long getEpochSecond() {
            return (Long) this.get(0);
        }

        public String getLogLine() {
            return (String) this.get(1);
        }
    }
}
