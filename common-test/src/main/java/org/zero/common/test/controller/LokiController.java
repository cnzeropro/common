package org.zero.common.test.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.api.extra.loki.LokiFeignClient;
import org.zero.common.api.extra.loki.model.request.LokiQueryRangeRequest;
import org.zero.common.data.model.view.Result;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/2
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/loki")
public class LokiController {
    private final LokiFeignClient lokiFeignClient;

    @RequestMapping("/test")
    public Result<Object> test() {
        // LokiDeleteRequest lokiDeleteRequest = LokiDeleteRequest.builder()
        //         .query("app=\"nginx\"")
        //         .maxInterval("1h")
        //         .build();
        // Object result = lokiFeignClient.formatQueryPost(LokiFormatQueryRequest.builder().query("{appname=\"persona\"}").build());
        LokiQueryRangeRequest queryRangeRequest = LokiQueryRangeRequest.builder()
			.start("1751299200000000000")
			.end("1751385600000000000")
                .query("{appname=\"persona\"}").build();
        Object result = lokiFeignClient.queryRange(queryRangeRequest);
        // LokiIndexStatsRequest indexStatsRequest = LokiIndexStatsRequest.builder()
        //         .query("{appname=\"persona\"}")
        //         .start(BigInteger.valueOf(1751299200000000000L))
        //         .end(BigInteger.valueOf(1751385600000000000L))
        //         .build();
        // Object result = lokiFeignClient.indexStatsPost(indexStatsRequest);
        // Object result = lokiFeignClient.logLevelPost(LokiLogLevelRequest.builder().logLevel("debug").build());
        return Result.ok(result);
    }
}
