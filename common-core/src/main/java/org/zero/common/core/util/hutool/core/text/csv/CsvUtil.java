package org.zero.common.core.util.hutool.core.text.csv;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.ObjectUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.zero.common.core.util.java.reflect.ClassUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/9/21
 */
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
        Collection<?> list = ListUtil.toList(data);
        if (CollUtil.allMatch(list, o -> ClassUtil.isAssignable(Map.class, o.getClass()))) {
            writer.writeBeans(list);
        } else if (CollUtil.allMatch(list, o -> ClassUtil.isJavaStrictBean(o.getClass()))) {
            writer.writeBeans(list);
        } else {
            writer.write(list);
        }
    }

    /**
     * 写入指定数据到文件
     *
     * @param filePath 文件路径
     */
    public void write(String filePath, Iterable<?> data) {
        @Cleanup CsvWriter writer = getWriter(filePath);
        write(writer, data);
    }

    /**
     * 写入指定数据到文件
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param data     数据
     */
    public void write(String filePath, CsvWriteConfig config, Iterable<?> data) {
        @Cleanup CsvWriter writer = getWriter(filePath, config);
        write(writer, data);
    }

    /* *********************************************************************** Read *********************************************************************** */

    /**
     * 读取全量数据，可能造成 OOM
     *
     * @param filePath 文件路径
     */
    public Collection<Collection<String>> list(String filePath) {
        return list(filePath, CsvReadConfig.defaultConfig());
    }

    /**
     * 读取全量数据，可能造成 OOM
     *
     * @param filePath 文件路径
     * @param config   配置
     */
    @SneakyThrows
    public Collection<Collection<String>> list(String filePath, CsvReadConfig config) {
        @Cleanup CsvReader reader = getReader(filePath, config);
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
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     */
    public Collection<Map<String, String>> listMap(String filePath) {
        return listMap(filePath, CsvReadConfig.defaultConfig());
    }

    /**
     * 读取全量数据，可能造成 OOM
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     * @param config   配置
     */
    @SneakyThrows
    public Collection<Map<String, String>> listMap(String filePath, CsvReadConfig config) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        return reader.read()
                .getRows()
                .stream()
                .map(CsvRow::getFieldMap)
                .collect(Collectors.toList());
    }

    /**
     * 分页读
     *
     * @param filePath 文件路径
     * @param pageNum  页码
     * @param pageSize 页大小
     */
    public Collection<Collection<String>> page(String filePath, long pageNum, long pageSize) {
        return page(filePath, CsvReadConfig.defaultConfig(), pageNum, pageSize);
    }

    /**
     * 分页读
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param pageNum  页码
     * @param pageSize 页大小
     */
    @SneakyThrows
    public Collection<Collection<String>> page(String filePath, CsvReadConfig config, long pageNum, long pageSize) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        return reader.stream()
                .skip((pageNum - 1L) * pageSize)
                .limit(pageSize)
                .map(CsvRow::getRawList)
                .collect(Collectors.toList());
    }

    /**
     * 分页读
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     * @param pageNum  页码
     * @param pageSize 页大小
     */
    public Collection<Map<String, String>> pageMap(String filePath, long pageNum, long pageSize) {
        return pageMap(filePath, CsvReadConfig.defaultConfig(), pageNum, pageSize);
    }

    /**
     * 分页读
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param pageNum  页码
     * @param pageSize 页大小
     */
    @SneakyThrows
    public Collection<Map<String, String>> pageMap(String filePath, CsvReadConfig config, long pageNum, long pageSize) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        return reader.stream()
                .skip((pageNum - 1L) * pageSize)
                .limit(pageSize)
                .map(CsvRow::getFieldMap)
                .collect(Collectors.toList());
    }

    /**
     * 统计总行数
     *
     * @param filePath 文件路径
     */
    public long count(String filePath) {
        return count(filePath, CsvReadConfig.defaultConfig());
    }

    /**
     * 统计总行数
     *
     * @param filePath 文件路径
     * @param config   配置
     */
    @SneakyThrows
    public long count(String filePath, CsvReadConfig config) {
        CsvReader reader = getReader(filePath, config);
        return reader.stream().count();
    }

    /**
     * 读取指定行号的数据
     *
     * @param filePath 文件路径
     * @param rowNum   行号。从 1 开始
     */
    public Collection<String> getRow(String filePath, long rowNum) {
        return getRow(filePath, CsvReadConfig.defaultConfig(), rowNum);
    }

    /**
     * 读取指定行号的数据
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param rowNum   行号。从 1 开始
     */
    @SneakyThrows
    public Collection<String> getRow(String filePath, CsvReadConfig config, long rowNum) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        return reader.stream()
                .skip(rowNum - 1L)
                .findFirst()
                .map(CsvRow::getRawList)
                .orElseGet(Collections::emptyList);
    }

    /**
     * 读取指定行号的数据
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     * @param rowNum   行号。从 1 开始
     */
    public Map<String, String> getRowMap(String filePath, long rowNum) {
        return getRowMap(filePath, CsvReadConfig.defaultConfig(), rowNum);
    }

    /**
     * 读取指定行号的数据
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param rowNum   行号。从 1 开始
     */
    @SneakyThrows
    public Map<String, String> getRowMap(String filePath, CsvReadConfig config, long rowNum) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        return reader.stream()
                .skip(rowNum - 1L)
                .findFirst()
                .map(CsvRow::getFieldMap)
                .orElseGet(Collections::emptyMap);
    }

    /**
     * 读取指定列号的数据
     *
     * @param filePath 文件路径
     * @param colNum   列号。从 1 开始
     */
    public Collection<String> getCol(String filePath, long colNum) {
        return getCol(filePath, CsvReadConfig.defaultConfig(), colNum);
    }

    /**
     * 读取指定列号的数据
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param colNum   列号。从 1 开始
     */
    @SneakyThrows
    public Collection<String> getCol(String filePath, CsvReadConfig config, long colNum) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        Collection<String> result = reader.stream()
                .map(CsvRow::getRawList)
                .map(row -> CollUtil.get(row, (int) colNum - 1))
                .collect(Collectors.toList());
        if (CollUtil.allMatch(result, Objects::isNull)) {
            return ListUtil.empty();
        }
        return result;
    }

    /**
     * 读取指定列名的数据
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     * @param colName  列名
     */
    public Collection<String> getCol(String filePath, String colName) {
        return getCol(filePath, CsvReadConfig.defaultConfig(), colName);
    }

    /**
     * 读取指定列名的数据
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath 文件路径
     * @param config   配置
     * @param colName  列名
     */
    @SneakyThrows
    public Collection<String> getCol(String filePath, CsvReadConfig config, String colName) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        Collection<String> result = reader.stream()
                .map(row -> row.getByName(colName))
                .collect(Collectors.toList());
        if (CollUtil.allMatch(result, Objects::isNull)) {
            return ListUtil.empty();
        }
        return result;
    }

    /**
     * 利用指定谓词过滤
     *
     * @param filePath  文件路径
     * @param predicate 谓词
     */
    @SneakyThrows
    public Collection<Collection<String>> filter(String filePath, Predicate<? super Collection<String>> predicate) {
        return filter(filePath, CsvReadConfig.defaultConfig(), predicate);
    }

    /**
     * 利用指定谓词过滤
     *
     * @param filePath  文件路径
     * @param config    配置
     * @param predicate 谓词
     */
    @SneakyThrows
    public Collection<Collection<String>> filter(String filePath, CsvReadConfig config, Predicate<? super Collection<String>> predicate) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        return reader.stream()
                .map(CsvRow::getRawList)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 利用指定谓词过滤
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath  文件路径
     * @param predicate 谓词
     */
    public Collection<Map<String, String>> filterMap(String filePath, Predicate<? super Map<String, String>> predicate) {
        return filterMap(filePath, CsvReadConfig.defaultConfig(), predicate);
    }

    /**
     * 利用指定谓词过滤
     * <p>
     * 如果没有表头，请勿使用此方法，否则抛出异常
     *
     * @param filePath  文件路径
     * @param config    配置
     * @param predicate 谓词
     */
    @SneakyThrows
    public Collection<Map<String, String>> filterMap(String filePath, CsvReadConfig config, Predicate<? super Map<String, String>> predicate) {
        @Cleanup CsvReader reader = getReader(filePath, config);
        return reader.stream()
                .map(CsvRow::getFieldMap)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /* *********************************************************************** Other *********************************************************************** */
    public static CsvWriter getWriter(String filePath) {
        return getWriter(filePath, CsvWriteConfig.defaultConfig());
    }

    public static CsvWriter getWriter(String filePath, CsvWriteConfig config) {
        CsvWriteConfig conf = ObjectUtil.defaultIfNull(config, CsvWriteConfig::defaultConfig);
        return new CsvWriter(filePath, conf.getCharset(), conf.isAppend(), conf);
    }

    public static CsvReader getReader(String filePath) {
        return getReader(filePath, CsvReadConfig.defaultConfig());
    }

    public static CsvReader getReader(String filePath, CsvReadConfig config) {
        CsvReadConfig conf = ObjectUtil.defaultIfNull(config, CsvReadConfig::defaultConfig);
        return new CsvReader(FileUtil.file(filePath), conf.getCharset(), conf);
    }
}