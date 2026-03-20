package org.zero.common.api.extra.loki;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
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

@Slf4j
@Component
public class LokiFeignFallbackFactory implements FallbackFactory<LokiFeignClient> {
    @Override
    public LokiFeignClient create(Throwable throwable) {
        log.warn("Unable to fetch the Loki service", throwable);
        return new LokiFeignClient() {
            @Override
            public LokiResponse<Void> push(LokiPushRequest pushRequest) {
                return LokiResponse.error();
            }

            @Override
            public LokiResponse<LokiQueryResponse> query(LokiQueryRequest queryRequest) {
                return LokiResponse.error();
            }

            @Override
            public LokiResponse<LokiQueryRangeResponse> queryRange(LokiQueryRangeRequest queryRangeRequest) {
                return LokiResponse.error();
            }

            @Override
            public LokiResponse<Collection<String>> labels(LokiLabelsRequest labelsRequest) {
                return LokiResponse.error();
            }

            @Override
            public LokiResponse<Collection<String>> labelValues(String name, LokiLabelValuesRequest labelValuesRequest) {
                return LokiResponse.error();
            }

            @Override
            public LokiResponse<Collection<LokiSeriesResponse>> seriesGet(LokiSeriesRequest seriesRequest) {
                return LokiResponse.error();
            }

            @Override
            public LokiResponse<Collection<LokiSeriesResponse>> seriesPost(LokiSeriesRequest seriesRequest) {
                return LokiResponse.error();
            }

            @Override
            public LokiIndexStatsResponse indexStatsGet(LokiIndexStatsRequest indexStatsRequest) {
                return LokiIndexStatsResponse.builder().build();
            }

            @Override
            public LokiIndexStatsResponse indexStatsPost(LokiIndexStatsRequest indexStatsRequest) {
                return LokiIndexStatsResponse.builder().build();
            }

            @Override
            public String ready() {
                return "";
            }

            @Override
            public LokiLogLevelResponse logLevelGet() {
                return LokiLogLevelResponse.builder().build();
            }

            @Override
            public LokiLogLevelResponse logLevelPost(LokiLogLevelRequest logLevelRequest) {
                return LokiLogLevelResponse.builder().build();
            }

            @Override
            public String metrics() {
                return "";
            }

            @Override
            public String config() {
                return "";
            }

            @Override
            public String config(LokiConfigRequest configRequest) {
                return "";
            }

            @Override
            public String services() {
                return "";
            }

            @Override
            public LokiStatusBuildInfoResponse statusBuildInfo() {
                return LokiStatusBuildInfoResponse.builder().build();
            }

            @Override
            public String flush() {
                return "";
            }

            @Override
            public String ingesterPrepareShutdownGet() {
                return "";
            }

            @Override
            public String ingesterPrepareShutdownPost() {
                return "";
            }

            @Override
            public String ingesterPrepareShutdownDelete() {
                return "";
            }

            @Override
            public String ingesterShutdown(LokiIngesterShutdownRequest ingesterShutdownRequest) {
                return "";
            }

            @Override
            public String ingesterShutdownPost(LokiIngesterShutdownRequest ingesterShutdownRequest) {
                return "";
            }

            @Override
            public String delete(LokiDeleteRequest deleteRequest) {
                return "";
            }

            @Override
            public String deletePut(LokiDeleteRequest deleteRequest) {
                return "";
            }

            @Override
            public String deleteGet() {
                return "";
            }

            @Override
            public String deleteCancel(LokiDeleteCancelRequest deleteCancelRequest) {
                return "";
            }

            @Override
            public LokiResponse<String> formatQueryGet(String query) {
                return LokiResponse.error();
            }

            @Override
            public LokiResponse<String> formatQueryPost(LokiFormatQueryRequest formatQueryRequest) {
                return LokiResponse.error();
            }
        };
    }
}
