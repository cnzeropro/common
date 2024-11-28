package org.zero.common.api.extra.loki;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.zero.common.api.extra.loki.model.request.LokiLabelValuesRequest;
import org.zero.common.api.extra.loki.model.request.LokiLabelsRequest;
import org.zero.common.api.extra.loki.model.request.LokiPushRequest;
import org.zero.common.api.extra.loki.model.request.LokiQueryRangeRequest;
import org.zero.common.api.extra.loki.model.request.LokiQueryRequest;
import org.zero.common.api.extra.loki.model.response.LokiQueryRangeResponse;
import org.zero.common.api.extra.loki.model.response.LokiQueryResponse;
import org.zero.common.api.extra.loki.model.response.LokiResult;

import java.util.List;

@Slf4j
@Component
public class LokiFeignFallbackFactory implements FallbackFactory<LokiFeignClient> {
    @Override
    public LokiFeignClient create(Throwable throwable) {
        log.warn("Unable to fetch the Loki service", throwable);
        return new LokiFeignClient() {
            @Override
            public LokiResult<Void> push(LokiPushRequest lokiPush) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<LokiQueryResponse> query(LokiQueryRequest lokiQuery) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<LokiQueryRangeResponse> queryRange(LokiQueryRangeRequest lokiQueryRange) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<List<String>> labels(LokiLabelsRequest lokiLabels) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<List<String>> labelValues(String name, LokiLabelValuesRequest lokiLabelValues) {
                return LokiResult.error();
            }
        };
    }
}
