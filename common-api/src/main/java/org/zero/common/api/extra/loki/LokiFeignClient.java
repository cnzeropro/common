package org.zero.common.api.extra.loki;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.zero.common.api.extra.loki.model.request.LokiConfigRequest;
import org.zero.common.api.extra.loki.model.request.LokiDeleteCancelRequest;
import org.zero.common.api.extra.loki.model.request.LokiDeleteRequest;
import org.zero.common.api.extra.loki.model.request.LokiFormatQueryRequest;
import org.zero.common.api.extra.loki.model.request.LokiIndexStatsRequest;
import org.zero.common.api.extra.loki.model.request.LokiIngesterShutdownRequest;
import org.zero.common.api.extra.loki.model.request.LokiLabelValuesRequest;
import org.zero.common.api.extra.loki.model.request.LokiLabelsRequest;
import org.zero.common.api.extra.loki.model.request.LokiLogLevelRequest;
import org.zero.common.api.extra.loki.model.request.LokiPushRequest;
import org.zero.common.api.extra.loki.model.request.LokiQueryRangeRequest;
import org.zero.common.api.extra.loki.model.request.LokiQueryRequest;
import org.zero.common.api.extra.loki.model.request.LokiSeriesRequest;
import org.zero.common.api.extra.loki.model.response.LokiIndexStatsResponse;
import org.zero.common.api.extra.loki.model.response.LokiLogLevelResponse;
import org.zero.common.api.extra.loki.model.response.LokiQueryRangeResponse;
import org.zero.common.api.extra.loki.model.response.LokiQueryResponse;
import org.zero.common.api.extra.loki.model.response.LokiResponse;
import org.zero.common.api.extra.loki.model.response.LokiSeriesResponse;
import org.zero.common.api.extra.loki.model.response.LokiStatusBuildInfoResponse;

import java.util.Collection;

/**
 * Loki Client
 * <p>
 * 封装 <a href="https://grafana.com/docs/enterprise-logs/latest/reference/loki-http-api/">Grafana Loki HTTP API</a>
 */
@FeignClient(name = "loki", url = "${api.loki.url:}",
        configuration = LokiFeignConfig.class,
        fallbackFactory = LokiFeignFallbackFactory.class)
public interface LokiFeignClient {
    String API_V1_PATH = "/loki/api/v1";

    /* **************************************************** Ingest endpoints **************************************************** */

    /**
     * 向 Loki 推送日志数据。
     */
    @PostMapping(API_V1_PATH + "/push")
    LokiResponse<Void> push(@RequestBody LokiPushRequest pushRequest);

    /* **************************************************** Query endpoints **************************************************** */

    /**
     * 查询单个时间点的日志或指标结果。
     */
    @GetMapping(API_V1_PATH + "/query")
    LokiResponse<LokiQueryResponse> query(@SpringQueryMap LokiQueryRequest queryRequest);

    /**
     * 查询时间范围内的日志或指标结果。
     */
    @GetMapping(API_V1_PATH + "/query_range")
    LokiResponse<LokiQueryRangeResponse> queryRange(@SpringQueryMap LokiQueryRangeRequest queryRangeRequest);

    /**
     * 查询标签名称。
     */
    @GetMapping(API_V1_PATH + "/labels")
    LokiResponse<Collection<String>> labels(@SpringQueryMap LokiLabelsRequest labelsRequest);

    /**
     * 查询指定标签的可选值。
     */
    @GetMapping(API_V1_PATH + "/label/{name}/values")
    LokiResponse<Collection<String>> labelValues(@PathVariable("name") String name,
                                                 @SpringQueryMap LokiLabelValuesRequest labelValuesRequest);

    /**
     * 通过 GET 查询 Series 标签集合。
     */
    @GetMapping(API_V1_PATH + "/series")
    LokiResponse<Collection<LokiSeriesResponse>> seriesGet(@SpringQueryMap LokiSeriesRequest seriesRequest);

    /**
     * 通过 POST 查询 Series 标签集合。
     */
    @PostMapping(value = API_V1_PATH + "/series", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiResponse<Collection<LokiSeriesResponse>> seriesPost(@RequestBody LokiSeriesRequest seriesRequest);

    /**
     * 通过 GET 查询索引统计信息。
     */
    @GetMapping(API_V1_PATH + "/index/stats")
    LokiIndexStatsResponse indexStatsGet(@SpringQueryMap LokiIndexStatsRequest indexStatsRequest);

    /**
     * 通过 POST 查询索引统计信息。
     */
    @PostMapping(value = API_V1_PATH + "/index/stats", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiIndexStatsResponse indexStatsPost(@RequestBody LokiIndexStatsRequest indexStatsRequest);

    /* **************************************************** Status endpoints **************************************************** */

    /**
     * 检查服务是否就绪。
     */
    @GetMapping("/ready")
    String ready();

    /**
     * 获取当前日志级别。
     */
    @GetMapping("/log_level")
    LokiLogLevelResponse logLevelGet();

    /**
     * 修改日志级别。
     */
    @PostMapping(value = "/log_level", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiLogLevelResponse logLevelPost(@RequestBody LokiLogLevelRequest logLevelRequest);

    /**
     * 获取 Prometheus 指标。
     */
    @GetMapping("/metrics")
    String metrics();

    /**
     * 获取当前配置。
     */
    @GetMapping("/config")
    String config();

    /**
     * 按指定模式获取配置。
     */
    @GetMapping("/config")
    String config(@SpringQueryMap LokiConfigRequest configRequest);

    /**
     * 列出当前服务。
     */
    @GetMapping("/services")
    String services();

    /**
     * 获取构建信息。
     */
    @GetMapping(API_V1_PATH + "/status/buildinfo")
    LokiStatusBuildInfoResponse statusBuildInfo();

    /* **************************************************** Ring endpoints **************************************************** */

    /* **************************************************** Flush/shutdown endpoints **************************************************** */

    /**
     * 执行 flush。
     */
    @PostMapping("/flush")
    String flush();

    /**
     * 查询 ingester 预关闭状态。
     */
    @GetMapping("/ingester/prepare_shutdown")
    String ingesterPrepareShutdownGet();

    /**
     * 标记 ingester 准备关闭。
     */
    @PostMapping("/ingester/prepare_shutdown")
    String ingesterPrepareShutdownPost();

    /**
     * 取消 ingester 预关闭状态。
     */
    @DeleteMapping("/ingester/prepare_shutdown")
    String ingesterPrepareShutdownDelete();

    /**
     * 通过 GET 关闭 ingester。
     */
    @GetMapping("/ingester/shutdown")
    String ingesterShutdown(@SpringQueryMap LokiIngesterShutdownRequest ingesterShutdownRequest);

    /**
     * 通过 POST 关闭 ingester。
     */
    @PostMapping(value = "/ingester/shutdown", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String ingesterShutdownPost(@RequestBody LokiIngesterShutdownRequest ingesterShutdownRequest);

    /* **************************************************** Rule endpoints **************************************************** */


    /* **************************************************** Log deletion endpoints **************************************************** */

    /**
     * 创建删除请求。
     */
    @PostMapping(API_V1_PATH + "/delete")
    String delete(@SpringQueryMap LokiDeleteRequest deleteRequest);

    /**
     * 以幂等方式创建或更新删除请求。
     */
    @PutMapping(API_V1_PATH + "/delete")
    String deletePut(@SpringQueryMap LokiDeleteRequest deleteRequest);

    /**
     * 查询当前删除请求列表。
     */
    @GetMapping(API_V1_PATH + "/delete")
    String deleteGet();

    /**
     * 取消指定删除请求。
     */
    @DeleteMapping(API_V1_PATH + "/delete")
    String deleteCancel(@SpringQueryMap LokiDeleteCancelRequest deleteCancelRequest);

    /* **************************************************** Other endpoints **************************************************** */

    /**
     * 通过 GET 格式化 LogQL 语句。
     */
    @GetMapping(API_V1_PATH + "/format_query")
    LokiResponse<String> formatQueryGet(@RequestParam("query") String query);

    /**
     * 通过 POST 格式化 LogQL 语句。
     */
    @PostMapping(value = API_V1_PATH + "/format_query", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiResponse<String> formatQueryPost(@RequestBody LokiFormatQueryRequest formatQueryRequest);
}
