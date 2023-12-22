package org.zero.common.api.extra.loki;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.zero.common.api.extra.loki.model.dto.LokiPushDTO;
import org.zero.common.api.extra.loki.model.qo.LokiLabelsQO;
import org.zero.common.api.extra.loki.model.qo.LokiQueryQO;
import org.zero.common.api.extra.loki.model.qo.LokiQueryRangeQO;
import org.zero.common.api.extra.loki.model.vo.LokiQueryRangeVO;
import org.zero.common.api.extra.loki.model.LokiResult;
import org.zero.common.api.extra.loki.model.vo.LokiQueryVO;

import java.util.List;

@Slf4j
@Component
public class LokiFeignFallbackFactory implements FallbackFactory<LokiFeignClient> {
    @Override
    public LokiFeignClient create(Throwable throwable) {
        log.warn("The Loki service request failed", throwable);
        return new LokiFeignClient() {
            @Override
            public LokiResult<LokiQueryVO> query(LokiQueryQO lokiQuery) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<LokiQueryRangeVO> queryRange(LokiQueryRangeQO lokiQueryRange) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<List<String>> labels(LokiLabelsQO lokiLabels) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<List<String>> labels(String name, LokiLabelsQO lokiLabels) {
                return LokiResult.error();
            }

            @Override
            public LokiResult<Void> push(LokiPushDTO lokiPush) {
                return LokiResult.error();
            }
        };
    }
}
