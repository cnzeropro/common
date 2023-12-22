package org.zero.common.api.extra.loki;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.zero.common.api.extra.loki.model.LokiResult;
import org.zero.common.api.extra.loki.model.dto.LokiPushDTO;
import org.zero.common.api.extra.loki.model.qo.LokiLabelsQO;
import org.zero.common.api.extra.loki.model.qo.LokiQueryQO;
import org.zero.common.api.extra.loki.model.qo.LokiQueryRangeQO;
import org.zero.common.api.extra.loki.model.vo.LokiQueryRangeVO;
import org.zero.common.api.extra.loki.model.vo.LokiQueryVO;

import java.util.List;

/**
 * LokiClient
 * <p>
 * 封装 <a href="https://grafana.com/docs/loki/latest/reference/api/">Grafana Loki HTTP API</a>
 */
@FeignClient(name = "loki", url = "${api.loki.url:}", fallbackFactory = LokiFeignFallbackFactory.class)
public interface LokiFeignClient {
    @GetMapping("/loki/api/v1/query")
    LokiResult<LokiQueryVO> query(@SpringQueryMap LokiQueryQO lokiQuery);

    @GetMapping("/loki/api/v1/query_range")
    LokiResult<LokiQueryRangeVO> queryRange(@SpringQueryMap LokiQueryRangeQO lokiQueryRange);

    @GetMapping("/loki/api/v1/labels")
    LokiResult<List<String>> labels(@SpringQueryMap LokiLabelsQO lokiLabels);

    @GetMapping("/loki/api/v1/label/{name}/values")
    LokiResult<List<String>> labels(@PathVariable String name, @SpringQueryMap LokiLabelsQO lokiLabels);

    @PostMapping("/loki/api/v1/push")
    LokiResult<Void> push(@RequestBody LokiPushDTO lokiPush);
}
