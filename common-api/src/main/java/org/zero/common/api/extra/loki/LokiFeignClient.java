package org.zero.common.api.extra.loki;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
 * 封装 <a href="https://grafana.com/docs/loki/latest/reference/api/">Grafana Loki HTTP API</a>
 */
@FeignClient(name = "loki", url = "${api.loki.url:}",
        configuration = LokiFeignConfig.class,
        fallbackFactory = LokiFeignFallbackFactory.class)
public interface LokiFeignClient {
    String API_V1_PATH = "/loki/api/v1";

    /* **************************************************** Ingest endpoints **************************************************** */

    /**
     * 向 Loki 发送日志数据
     */
    @PostMapping(API_V1_PATH + "/push")
    LokiResponse<Void> push(@RequestBody LokiPushRequest pushRequest);

    /* **************************************************** Query endpoints **************************************************** */

    /**
     * 查询单个时间节点的日志数据
     */
    @GetMapping(API_V1_PATH + "/query")
    LokiResponse<LokiQueryResponse> query(@SpringQueryMap LokiQueryRequest queryRequest);

    /**
     * 查询时间范围内的日志数据
     */
    @GetMapping(API_V1_PATH + "/query_range")
    LokiResponse<LokiQueryRangeResponse> queryRange(@SpringQueryMap LokiQueryRangeRequest queryRangeRequest);

    /**
     * 查询标签
     */
    @GetMapping(API_V1_PATH + "/labels")
    LokiResponse<Collection<String>> labels(@SpringQueryMap LokiLabelsRequest labelsRequest);

    /**
     * 查询标签值
     */
    @GetMapping(API_V1_PATH + "/label/{name}/values")
    LokiResponse<Collection<String>> labelValues(@PathVariable("name") String name, @SpringQueryMap LokiLabelValuesRequest labelValuesRequest);

    /**
     * 查询 Series（GET）
     */
    @GetMapping(API_V1_PATH + "/series")
    LokiResponse<Collection<LokiSeriesResponse>> seriesGet(@SpringQueryMap LokiSeriesRequest seriesRequest);

    /**
     * 查询 Series（POST）
     */
    @PostMapping(path = API_V1_PATH + "/series", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiResponse<Collection<LokiSeriesResponse>> seriesPost(@RequestBody LokiSeriesRequest seriesRequest);

    /**
     * 查询索引统计信息（GET）
     */
    @GetMapping(API_V1_PATH + "/index/stats")
    LokiIndexStatsResponse indexStatsGet(@SpringQueryMap LokiIndexStatsRequest indexStatsRequest);

    /**
     * 索引统计信息（POST）
     */
    @PostMapping(path = API_V1_PATH + "/index/stats", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiIndexStatsResponse indexStatsPost(@RequestBody LokiIndexStatsRequest indexStatsRequest);

    /* **************************************************** Status endpoints **************************************************** */

    /**
     * 检查服务是否可用
     */
    @GetMapping("/ready")
    String ready();

    /**
     * 更改日志级别（GET）
     */
    @GetMapping("/log_level")
    LokiLogLevelResponse logLevelGet(@RequestParam("log_level") String logLevel);

    /**
     * 更改日志级别（POST）
     */
    @PostMapping(path = "/log_level", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiLogLevelResponse logLevelPost(@RequestBody LokiLogLevelRequest logLevelRequest);

    /**
     * 获取服务指标
     */
    @GetMapping("/metrics")
    String metrics();

    /**
     * 获取当前配置
     */
    @GetMapping("/config")
    String config();

    /**
     * 列出当前服务
     */
    @GetMapping("/services")
    String services();

    /**
     * 显示构建信息
     */
    @GetMapping(API_V1_PATH + "/status/buildinfo")
    LokiStatusBuildInfoResponse statusBuildInfo();

    /* **************************************************** Ring endpoints **************************************************** */

    /* **************************************************** Flush/shutdown endpoints **************************************************** */

    /**
     * 刷新数据
     */
    @PostMapping("/flush")
    String flush();

    /**
     * ingester 准备关闭（GET）
     */
    @GetMapping("/ingester/prepare_shutdown")
    String ingesterPrepareShutdownGet();

    /**
     * ingester 准备关闭（POST）
     */
    @PostMapping("/ingester/prepare_shutdown")
    String ingesterPrepareShutdownPost();

    /**
     * ingester 准备关闭（DELETE）
     */
    @DeleteMapping("/ingester/prepare_shutdown")
    String ingesterPrepareShutdownDelete();

    /**
     * ingester 关闭（GET）
     */
    @GetMapping("/ingester/shutdown")
    String ingesterShutdown(@SpringQueryMap LokiIngesterShutdownRequest ingesterShutdownRequest);

    /* **************************************************** Rule endpoints **************************************************** */


    /* **************************************************** Log deletion endpoints **************************************************** */

    @PostMapping(API_V1_PATH + "/delete")
    String delete(@SpringQueryMap LokiDeleteRequest deleteRequest);

    /* **************************************************** Other endpoints **************************************************** */

    /**
     * 格式化 LogQL 查询语句（GET）
     */
    @GetMapping(API_V1_PATH + "/format_query")
    LokiResponse<String> formatQueryGet(@RequestParam("query") String query);

    /**
     * 格式化 LogQL 查询语句（POST）
     */
    @PostMapping(path = API_V1_PATH + "/format_query", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LokiResponse<String> formatQueryPost(@RequestBody LokiFormatQueryRequest formatQueryRequest);
}
