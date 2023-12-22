package org.zero.common.core.util.java.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.data.exception.UtilException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/9/21
 */
@Slf4j
@UtilityClass
public class CsvUtil {
    public final String DEFAULT_DELIMITER = ",";

    public final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /* *********************************************************************** 写 *********************************************************************** */

    /**
     * 写入指定数据到文件，覆盖模式
     */
    public void write(String filePath, List<Map<String, Object>> dataMapList) {
        write(filePath, dataMapList, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 写入指定数据到文件，覆盖模式
     */
    public void write(String filePath, List<Map<String, Object>> dataMapList, String delimiter, Charset charset) {
        String header = CollUtil.join(dataMapList.get(0).keySet(), delimiter);
        FileUtil.writeString(warp(header), filePath, charset);
        dataMapList.forEach(map -> {
            String data = CollUtil.join(map.values(), delimiter);
            FileUtil.appendString(warp(data), filePath, charset);
        });
    }

    /**
     * 写入指定数据到文件，追加模式
     */
    public void append(String filePath, List<Map<String, Object>> dataMapList) {
        append(filePath, dataMapList, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 写入指定数据到文件，追加模式
     */
    public void append(String filePath, List<Map<String, Object>> dataMapList, String delimiter, Charset charset) {
        dataMapList.forEach(map -> {
            String data = CollUtil.join(map.values(), delimiter);
            FileUtil.appendString(warp(data), filePath, charset);
        });
    }

    /* *********************************************************************** 读 *********************************************************************** */

    /**
     * 读取全量数据，可能造成OOM
     */
    public List<Map<String, Object>> readAll(String filePath) {
        return readAll(filePath, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取全量数据，可能造成OOM
     */
    public List<Map<String, Object>> readAll(String filePath, String delimiter, Charset charset) {
        String[] headers = readHeader(filePath);
        try (Stream<String> stream = getStream(filePath, charset)) {
            List<String> dataList = stream.skip(1L).collect(Collectors.toList());
            return merge(headers, dataList, delimiter);
        }
    }

    /**
     * 分页读
     */
    public List<Map<String, Object>> page(String filePath, int pageNum, int pageSize) {
        return page(filePath, pageNum, pageSize, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 分页读
     */
    public List<Map<String, Object>> page(String filePath, int pageNum, int pageSize, String delimiter, Charset charset) {
        String[] headers = readHeader(filePath);
        try (Stream<String> stream = getStream(filePath, charset)) {
            List<String> dataList = stream.skip((long) pageNum * pageSize + 1L).limit(pageSize).collect(Collectors.toList());
            return merge(headers, dataList, delimiter);
        }
    }

    /**
     * 统计csv文件总行数
     */
    public long count(String filePath) {
        return count(filePath, DEFAULT_CHARSET);
    }

    /**
     * 统计csv文件总行数
     */
    public long count(String filePath, Charset charset) {
        try (Stream<String> stream = getStream(filePath, charset)) {
            return stream.count();
        }
    }

    /**
     * 读取指定行号的数据
     */
    public List<String> readRow(String filePath, int rowNum) {
        return readRow(filePath, rowNum, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取指定行号的数据
     */
    public List<String> readRow(String filePath, int rowNum, String delimiter, Charset charset) {
        try (Stream<String> stream = getStream(filePath, charset)) {
            Optional<String> firstOpt = stream.skip(rowNum - 1L).findFirst();
            if (!firstOpt.isPresent()) {
                return ListUtil.empty();
            }
            return CharSequenceUtil.split(firstOpt.get(), delimiter);
        }
    }

    /**
     * 读取指定列号的数据
     */
    public List<String> readCol(String filePath, int colNum, boolean withHeader) {
        return readCol(filePath, colNum, withHeader, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取指定列号的数据
     */
    public List<String> readCol(String filePath, int colNum, boolean withHeader, String delimiter, Charset charset) {
        try (Stream<String> stream = getStream(filePath, charset)) {
            Stream<String> tempStream = stream;
            if (!withHeader) {
                tempStream = stream.skip(1L);
            }
            return tempStream.map(s -> {
                String[] data = CharSequenceUtil.splitToArray(s, delimiter);
                try {
                    return data[colNum];
                } catch (Exception e) {
                    throw new UtilException(String.format("Out of bounds, data len: %d, query col: %d", data.length, colNum));
                }
            }).collect(Collectors.toList());
        }
    }

    /**
     * 读取指定列名的数据
     */
    public List<String> readCol(String filePath, String colName) {
        return readCol(filePath, colName, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取指定列名的数据
     */
    public List<String> readCol(String filePath, String colName, String delimiter, Charset charset) {
        String[] headers = readHeader(filePath);
        try (Stream<String> stream = getStream(filePath, charset)) {
            return stream.skip(1L).map(s -> {
                String[] data = CharSequenceUtil.splitToArray(s, delimiter);
                Map<String, Object> dataMap = merge(headers, data);
                return StrUtil.utf8Str(dataMap.get(colName));
            }).collect(Collectors.toList());
        }
    }

    public String[] readHeader(String filePath) {
        return readHeader(filePath, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    public String[] readHeader(String filePath, String delimiter, Charset charset) {
        String[] headers = {};
        try (Stream<String> stream = getStream(filePath, charset)) {
            Optional<String> firstOpt = stream.findFirst();
            if (!firstOpt.isPresent()) {
                return headers;
            }
            headers = CharSequenceUtil.splitToArray(firstOpt.get(), delimiter);
        }
        return headers;
    }

    /**
     * 利用指定谓词过滤
     */
    public List<Map<String, Object>> filter(String filePath, Predicate<? super Map<String, Object>> predicate) {
        return filter(filePath, predicate, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 利用指定谓词过滤
     */
    public List<Map<String, Object>> filter(String filePath, Predicate<? super Map<String, Object>> predicate, String delimiter, Charset charset) {
        String[] header = readHeader(filePath);
        try (Stream<String> stream = getStream(filePath, charset)) {
            return stream.map(s -> merge(header, s, delimiter)).filter(predicate).collect(Collectors.toList());
        }
    }

    /* *********************************************************************** 其他 *********************************************************************** */

    public List<Map<String, Object>> merge(String[] headers, List<String> dataList, String delimiter) {
        List<Map<String, Object>> result = ListUtil.list(true);
        for (String data : dataList) {
            result.add(merge(headers, data, delimiter));
        }
        return result;
    }

    public Map<String, Object> merge(String[] headers, String dataStr, String delimiter) {
        String[] data = CharSequenceUtil.splitToArray(dataStr, delimiter);
        return merge(headers, data);
    }

    public Map<String, Object> merge(String[] headers, String[] data) {
        Map<String, Object> map = MapUtil.newHashMap(16, true);
        for (int i = 0; i < headers.length || i < data.length; i++) {
            try {
                map.put(headers[i], data[i]);
            } catch (Exception e) {
                log.warn("csv data is misaligned");
                log.info("len: {}, headers: {}", headers.length, headers);
                log.info("len: {}, data: {}", data.length, data);
                throw new UtilException(String.format("Csv data is misaligned, header len: %d, data len: %d", headers.length, data.length));
            }
        }
        return map;
    }

    public Stream<String> getStream(String filePath, Charset charset) {
        return getStream(Paths.get(filePath), charset);
    }

    @SneakyThrows
    public Stream<String> getStream(Path filePath, Charset charset) {
        return Files.lines(filePath, charset);
    }

    private String warp(String data) {
        return data + "\n";
    }
}