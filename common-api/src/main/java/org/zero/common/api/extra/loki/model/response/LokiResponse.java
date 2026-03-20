package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.zero.common.data.model.transfer.BaseResult;

/**
 * Loki 通用响应包装。
 * <p>
 * 对应 Loki 大部分 HTTP API 返回的统一外层结构，通常包含 {@code status} 与 {@code data} 两部分。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class LokiResponse<T> implements BaseResult {
    /**
     * {@code status} - 响应状态，常见值为 {@code success} 或 {@code error}。
     */
    private String status;
    /**
     * {@code data} - 实际业务数据负载。
     */
    private T data;

    @Override
    public boolean isSuccess() {
        return "success".equals(status);
    }

    public static <T> LokiResponse<T> error() {
        return LokiResponse.<T>builder().status("error").build();
    }
}
