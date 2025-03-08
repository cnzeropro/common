package org.zero.common.api.extra.loki;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.zero.common.api.extra.loki.model.request.LokiLabelValuesRequest;
import org.zero.common.api.extra.loki.model.request.LokiLabelsRequest;
import org.zero.common.api.extra.loki.model.request.LokiPushRequest;
import org.zero.common.api.extra.loki.model.request.LokiQueryRangeRequest;
import org.zero.common.api.extra.loki.model.request.LokiQueryRequest;
import org.zero.common.api.extra.loki.model.response.LokiQueryRangeResponse;
import org.zero.common.api.extra.loki.model.response.LokiQueryResponse;
import org.zero.common.api.extra.loki.model.response.LokiResult;

import java.util.List;

/**
 * Loki Client
 * <p>
 * 封装 <a href="https://grafana.com/docs/loki/latest/reference/api/">Grafana Loki HTTP API</a>
 */
@FeignClient(name = "loki", url = "${api.loki.url:}",
        configuration = LokiFeignConfig.class,
        fallbackFactory = LokiFeignFallbackFactory.class)
public interface LokiFeignClient {
    /* **************************************************** Ingest endpoints **************************************************** */

    /**
     * 写入日志数据
     */
    @PostMapping("/loki/api/v1/push")
    LokiResult<Void> push(@RequestBody LokiPushRequest pushRequest);

    /* **************************************************** Query endpoints **************************************************** */

    /**
     * 查询单个时间节点的日志数据
     */
    @GetMapping("/loki/api/v1/query")
    LokiResult<LokiQueryResponse> query(@SpringQueryMap LokiQueryRequest queryRequest);

    /**
     * 查询时间范围内的日志数据
     */
    @GetMapping("/loki/api/v1/query_range")
    LokiResult<LokiQueryRangeResponse> queryRange(@SpringQueryMap LokiQueryRangeRequest queryRangeRequest);

    /**
     * 查询标签
     */
    @GetMapping("/loki/api/v1/labels")
    LokiResult<List<String>> labels(@SpringQueryMap LokiLabelsRequest labelsRequest);

    /**
     * 查询标签值
     */
    @GetMapping("/loki/api/v1/label/{name}/values")
    LokiResult<List<String>> labelValues(@PathVariable String name, @SpringQueryMap LokiLabelValuesRequest labelValuesRequest);
}
