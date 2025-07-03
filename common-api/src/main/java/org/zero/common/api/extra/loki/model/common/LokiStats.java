package org.zero.common.api.extra.loki.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiStats implements Serializable {
    private Ingester ingester;
    private Summary summary;
    private Store store;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Ingester implements Serializable {
        /**
         * Total bytes of compressed chunks (blocks) processed by ingesters
         */
        private Long compressedBytes;

        /**
         * Total bytes decompressed and processed by ingesters
         */
        private Long decompressedBytes;

        /**
         * Total lines decompressed and processed by ingesters
         */
        private Long decompressedLines;

        /**
         * Total bytes read from ingesters head chunks
         */
        private Long headChunkBytes;

        /**
         * Total lines read from ingesters head chunks
         */
        private Long headChunkLines;

        /**
         * Total batches sent by ingesters
         */
        private Long totalBatches;

        /**
         * Total chunks matched by ingesters
         */
        private Long totalChunksMatched;

        /**
         * Total of duplicates found by ingesters
         */
        private Long totalDuplicates;

        /**
         * Total lines sent by ingesters
         */
        private Long totalLinesSent;

        /**
         * Amount of ingesters reached
         */
        private Long totalReached;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Summary implements Serializable {
        /**
         * Total of bytes processed per second
         */
        private Long bytesProcessedPerSecond;
        /**
         * Total execution time in seconds (float)
         */
        private Float execTime;
        /**
         * Total lines processed per second
         */
        private Long linesProcessedPerSecond;
        /**
         * Total queue time in seconds (float)
         */
        private Float queueTime;
        /**
         * Total amount of bytes processed overall for this request
         */
        private Long totalBytesProcessed;
        /**
         * Total amount of lines processed overall for this request
         */
        private Long totalLinesProcessed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Store implements Serializable {
        /**
         * Total bytes of compressed chunks (blocks) processed by the store
         */
        private Long compressedBytes;

        /**
         * Total bytes decompressed and processed by the store
         */
        private Long decompressedBytes;

        /**
         * Total lines decompressed and processed by the store
         */
        private Long decompressedLines;

        /**
         * Total time spent downloading chunks in seconds (float)
         */
        private Float chunksDownloadTime;

        /**
         * Total chunks found in the index for the current query
         */
        private Long totalChunksRef;

        /**
         * Total of chunks downloaded
         */
        private Long totalChunksDownloaded;

        /**
         * Total of duplicates removed from replication
         */
        private Long totalDuplicates;
    }
}