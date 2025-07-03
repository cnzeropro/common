package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.zero.common.data.model.transfer.BaseResult;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class LokiResponse<T> implements BaseResult {
    private String status;
    private T data;

    @Override
    public boolean isSuccess() {
        return "success".equals(status);
    }

    public static <T> LokiResponse<T> error() {
        return LokiResponse.<T>builder().status("error").build();
    }
}