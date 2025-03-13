package org.zero.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.ArrayUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    /* *********************************************************************** Write *********************************************************************** */

    /**
     * 写入指定数据到文件
     *
     * @param writer 写出器
     * @param data   数据
     */
    public void write(CsvWriter writer, Iterable<?> data) {
        writer.writeBeans(data)
                .flush();
    }

    /**
     * 写入指定数据到文件
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param data     数据
     */
    public void write(String filePath, CsvWriteConfig config, Iterable<?> data) {
        @Cleanup CsvWriter writer = new CsvWriter(filePath, config.getCharset(), config.isAppend(), config);
        write(writer, data);
    }

    /* *********************************************************************** Read *********************************************************************** */

    /**
     * 读取全量数据，可能造成 OOM
     *
     * @param filePath 文件路径
     */
    public Collection<Collection<String>> readAll(CsvReader reader) {
        CsvData csvData = reader.read();
        Collection<Collection<String>> lines = ListUtil.list(false);
        List<String> header = csvData.getHeader();
        if (CollUtil.isNotEmpty(header)) {
            lines.add(header);
        }
        List<List<String>> data = csvData.getRows()
                .stream()
                .map(CsvRow::getRawList)
                .collect(Collectors.toList());
        lines.addAll(data);
        return lines;
    }

    /**
     * 读取全量数据，可能造成 OOM
     *
     * @param filePath  文件路径
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public List<List<String>> readAll(String filePath, String delimiter, Charset charset) {
        return getStream(filePath, charset).map(s -> CharSequenceUtil.split(s, delimiter))
                .collect(Collectors.toList());
    }

    /**
     * 读取全量数据，可能造成 OOM
     * <p>
     * 默认第一行为表头，作为 Map key。如果没有表头，请勿使用此方法
     *
     * @param filePath 文件路径
     */
    public List<Map<String, String>> readMapAll(String filePath) {
        return readMapAll(filePath, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取全量数据，可能造成 OOM
     * <p>
     * 默认第一行为表头，作为 Map key。如果没有表头，请勿使用此方法
     *
     * @param filePath  文件路径
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public List<Map<String, String>> readMapAll(String filePath, String delimiter, Charset charset) {
        String[] headers = readHeader(filePath);
        List<String> dataList = getStream(filePath, charset)
                .skip(1L)
                .collect(Collectors.toList());
        return merge(headers, dataList, delimiter);
    }

    /**
     * 分页读
     * <p>
     * 默认第一行为表头，作为 Map key。如果没有表头，请勿使用此方法
     *
     * @param filePath 文件路径
     * @param pageNum  页码
     * @param pageSize 页大小
     */
    public List<Map<String, String>> page(String filePath, long pageNum, long pageSize) {
        return page(filePath, pageNum, pageSize, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 分页读
     * <p>
     * 默认第一行为表头，作为 Map key。如果没有表头，请勿使用此方法
     *
     * @param filePath  文件路径
     * @param pageNum   页码
     * @param pageSize  页大小
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public List<Map<String, String>> page(String filePath, long pageNum, long pageSize, String delimiter, Charset charset) {
        String[] headers = readHeader(filePath);
        List<String> dataList = getStream(filePath, charset)
                .skip((pageNum - 1L) * pageSize + 1L)
                .limit(pageSize)
                .collect(Collectors.toList());
        return merge(headers, dataList, delimiter);

    }

    /**
     * 统计文件总行数
     * <p>
     * 包含表头
     *
     * @param filePath 文件路径
     */
    public long count(String filePath) {
        return count(filePath, DEFAULT_CHARSET);
    }

    /**
     * 统计文件总行数
     * <p>
     * 包含表头
     *
     * @param filePath 文件路径
     * @param charset  字符集
     */
    public long count(String filePath, Charset charset) {
        return getStream(filePath, charset).count();
    }

    /**
     * 读取指定行号的数据
     *
     * @param filePath 文件路径
     * @param rowNum   行号。从 1 开始
     */
    public List<String> readRow(String filePath, long rowNum) {
        return readRow(filePath, rowNum, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取指定行号的数据
     *
     * @param filePath  文件路径
     * @param rowNum    行号。从 1 开始
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public List<String> readRow(String filePath, long rowNum, String delimiter, Charset charset) {
        Optional<String> firstOpt = getStream(filePath, charset)
                .skip(rowNum)
                .findFirst();
        if (firstOpt.isPresent()) {
            return CharSequenceUtil.split(firstOpt.get(), delimiter);
        }
        return ListUtil.empty();
    }

    /**
     * 读取指定列号的数据
     *
     * @param filePath 文件路径
     * @param colNum   列号。从 1 开始
     */
    public List<String> readCol(String filePath, long colNum) {
        return readCol(filePath, colNum, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取指定列号的数据
     *
     * @param filePath  文件路径
     * @param colNum    列号。从 1 开始
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public List<String> readCol(String filePath, long colNum, String delimiter, Charset charset) {
        List<String> dataList = getStream(filePath, charset)
                .map(s -> {
                    List<String> data = CharSequenceUtil.split(s, delimiter);
                    return CollUtil.get(data, (int) colNum + 1);
                })
                .collect(Collectors.toList());
        if (CollUtil.allMatch(dataList, Objects::isNull)) {
            return ListUtil.empty();
        }
        return dataList;
    }

    /**
     * 读取指定列名的数据
     *
     * @param filePath 文件路径
     * @param colName  列名
     */
    public List<String> readCol(String filePath, String colName) {
        return readCol(filePath, colName, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 读取指定列名的数据
     *
     * @param filePath  文件路径
     * @param colName   列名
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public List<String> readCol(String filePath, String colName, String delimiter, Charset charset) {
        String[] headers = readHeader(filePath);
        return getStream(filePath, charset)
                .skip(1L)
                .map(s -> {
                    String[] data = CharSequenceUtil.splitToArray(s, delimiter);
                    Map<String, String> dataMap = merge(headers, data);
                    return dataMap.get(colName);
                })
                .collect(Collectors.toList());
    }

    /**
     * 读取表头
     *
     * @param filePath 文件路径
     */
    public String[] readHeader(String filePath) {
        return readHeader(filePath, DEFAULT_DELIMITER);
    }

    /**
     * 读取表头
     *
     * @param filePath  文件路径
     * @param delimiter 分隔符
     */
    public String[] readHeader(String filePath, String delimiter) {
        return readHeader(filePath, delimiter, DEFAULT_CHARSET);
    }

    /**
     * 读取表头
     *
     * @param filePath  文件路径
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public String[] readHeader(String filePath, String delimiter, Charset charset) {
        Optional<String> firstOpt = getStream(filePath, charset)
                .findFirst();
        if (firstOpt.isPresent()) {
            return CharSequenceUtil.splitToArray(firstOpt.get(), delimiter);
        }
        return new String[0];
    }

    /**
     * 利用指定谓词过滤
     *
     * @param filePath  文件路径
     * @param predicate 谓词
     */
    public List<Map<String, String>> filter(String filePath, Predicate<? super Map<String, String>> predicate) {
        return filter(filePath, predicate, DEFAULT_DELIMITER, DEFAULT_CHARSET);
    }

    /**
     * 利用指定谓词过滤
     *
     * @param filePath  文件路径
     * @param predicate 谓词
     * @param delimiter 分隔符
     * @param charset   字符集
     */
    public List<Map<String, String>> filter(String filePath, Predicate<? super Map<String, String>> predicate, String delimiter, Charset charset) {
        String[] header = readHeader(filePath);
        return getStream(filePath, charset)
                .skip(1L)
                .map(s -> merge(header, s, delimiter))
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /* *********************************************************************** Other *********************************************************************** */

    public List<Map<String, String>> merge(String[] headers, List<String> dataList, String delimiter) {
        return dataList.stream()
                .map(data -> merge(headers, data, delimiter))
                .collect(Collectors.toList());
    }

    public Map<String, String> merge(String[] headers, String dataStr, String delimiter) {
        String[] data = CharSequenceUtil.splitToArray(dataStr, delimiter);
        return merge(headers, data);
    }

    public Map<String, String> merge(String[] headers, String[] data) {
        int size = Math.max(headers.length, data.length);
        Map<String, String> map = MapUtil.newHashMap(size, true);
        for (int i = 0; i < size; i++) {
            map.put(ArrayUtil.get(headers, i), ArrayUtil.get(data, i));
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
        return data + System.lineSeparator();
    }
}