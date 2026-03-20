package org.zero.common.api.extra.loki.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Loki 查询统计信息。
 * <p>
 * 对应 Loki 查询响应中的 {@code stats} 字段，包含 ingester、summary、store 等统计数据。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiStats implements Serializable {
    /**
     * {@code ingester} - 查询命中 ingester 时产生的统计信息。
     */
    private Ingester ingester;
    /**
     * {@code summary} - 本次查询的汇总统计信息。
     */
    private Summary summary;
    /**
     * {@code store} - 查询命中持久化存储时产生的统计信息。
     */
    private Store store;

    /**
     * Ingester 统计信息。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Ingester implements Serializable {
        /**
         * {@code compressedBytes} - ingester 处理的压缩 chunk / block 总字节数。
         */
        private Long compressedBytes;

        /**
         * {@code decompressedBytes} - ingester 解压并处理的总字节数。
         */
        private Long decompressedBytes;

        /**
         * {@code decompressedLines} - ingester 解压并处理的总行数。
         */
        private Long decompressedLines;

        /**
         * {@code headChunkBytes} - 从 ingester head chunk 读取的总字节数。
         */
        private Long headChunkBytes;

        /**
         * {@code headChunkLines} - 从 ingester head chunk 读取的总行数。
         */
        private Long headChunkLines;

        /**
         * {@code totalBatches} - ingester 发送的总批次数。
         */
        private Long totalBatches;

        /**
         * {@code totalChunksMatched} - ingester 命中的 chunk 总数。
         */
        private Long totalChunksMatched;

        /**
         * {@code totalDuplicates} - ingester 发现的重复数据总数。
         */
        private Long totalDuplicates;

        /**
         * {@code totalLinesSent} - ingester 返回的总日志行数。
         */
        private Long totalLinesSent;

        /**
         * {@code totalReached} - 实际访问到的 ingester 数量。
         */
        private Long totalReached;
    }

    /**
     * 查询汇总统计信息。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Summary implements Serializable {
        /**
         * {@code bytesProcessedPerSecond} - 每秒处理的字节数。
         */
        private Long bytesProcessedPerSecond;
        /**
         * {@code execTime} - 查询执行总耗时，单位为秒。
         */
        private Float execTime;
        /**
         * {@code linesProcessedPerSecond} - 每秒处理的日志行数。
         */
        private Long linesProcessedPerSecond;
        /**
         * {@code queueTime} - 查询排队耗时，单位为秒。
         */
        private Float queueTime;
        /**
         * {@code totalBytesProcessed} - 本次请求累计处理的总字节数。
         */
        private Long totalBytesProcessed;
        /**
         * {@code totalLinesProcessed} - 本次请求累计处理的总日志行数。
         */
        private Long totalLinesProcessed;
    }

    /**
     * 存储层统计信息。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Store implements Serializable {
        /**
         * {@code compressedBytes} - 存储层处理的压缩 chunk / block 总字节数。
         */
        private Long compressedBytes;

        /**
         * {@code decompressedBytes} - 存储层解压并处理的总字节数。
         */
        private Long decompressedBytes;

        /**
         * {@code decompressedLines} - 存储层解压并处理的总日志行数。
         */
        private Long decompressedLines;

        /**
         * {@code chunksDownloadTime} - 下载 chunk 的总耗时，单位为秒。
         */
        private Float chunksDownloadTime;

        /**
         * {@code totalChunksRef} - 当前查询在索引中命中的 chunk 引用总数。
         */
        private Long totalChunksRef;

        /**
         * {@code totalChunksDownloaded} - 实际下载的 chunk 总数。
         */
        private Long totalChunksDownloaded;

        /**
         * {@code totalDuplicates} - 复制去重后移除的重复数据总数。
         */
        private Long totalDuplicates;
    }
}
