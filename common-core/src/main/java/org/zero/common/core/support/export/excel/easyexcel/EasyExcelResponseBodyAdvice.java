package org.zero.common.core.support.export.excel.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zero.common.core.support.export.excel.BasePoiResponseBodyAdvice;
import org.zero.common.core.support.export.excel.ExcelExport;
import org.zero.common.core.support.export.excel.ExcelFileType;
import org.zero.common.data.exception.CommonException;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/4
 */
@NoArgsConstructor
@ControllerAdvice
public class EasyExcelResponseBodyAdvice extends BasePoiResponseBodyAdvice {
    public EasyExcelResponseBodyAdvice(String... packageNames) {
        super(packageNames);
    }

    public EasyExcelResponseBodyAdvice(int order, String... packageNames) {
        super(order, packageNames);
    }

    public EasyExcelResponseBodyAdvice(String regex) {
        super(regex);
    }

    public EasyExcelResponseBodyAdvice(int order, String regex) {
        super(order, regex);
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfEntity(ExcelExport excelExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        Class<?> clazz = this.getHeadClass(collection);
        WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).head(clazz).build();
        excelWriter.write(collection, writeSheet);
        excelWriter.close();
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfBase(ExcelExport excelExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).build();
        // 包装一下，让其写一行，而不是写一列
        Collection<?> data = Collections.singletonList(collection);
        excelWriter.write(data, writeSheet);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfEntity(ExcelExport excelExport, Collection<Collection<?>> collections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        int i = 0;
        for (Collection<?> collection : collections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, i + 1);
            Class<?> clazz = this.getHeadClass(collection);
            WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).head(clazz).build();
            excelWriter.write(collection, writeSheet);
            i++;
        }
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfBase(ExcelExport excelExport, Collection<Collection<?>> collections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).build();
        excelWriter.write(collections, writeSheet);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfMap(ExcelExport excelExport, Collection<Map<?, ?>> maps) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        List<List<String>> head = this.getHeadList(maps);
        WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).head(head).build();
        List<Map<Integer, Object>> data = maps.stream().map(this::convert).collect(Collectors.toList());
        excelWriter.write(data, writeSheet);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfCollection(ExcelExport excelExport, Collection<Collection<Collection<?>>> collectionOfCollections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        int i = 0;
        for (Collection<Collection<?>> collections : collectionOfCollections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, i + 1);
            WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).build();
            excelWriter.write(collections, writeSheet);
            i++;
        }
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfMap(ExcelExport excelExport, Collection<Collection<Map<?, ?>>> collectionOfMaps) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        int i = 0;
        for (Collection<Map<?, ?>> maps : collectionOfMaps) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, i + 1);
            List<List<String>> head = this.getHeadList(maps);
            WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).head(head).build();
            List<Map<Integer, Object>> data = maps.stream().map(this::convert).collect(Collectors.toList());
            excelWriter.write(data, writeSheet);
            i++;
        }
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfOther(ExcelExport excelExport, Map<?, ?> map) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        List<List<String>> head = this.getHeadList(map);
        WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).head(head).build();
        // 包装一下，让其写一行，而不是写一列
        Collection<?> data = Collections.singletonList(map.values());
        excelWriter.write(data, writeSheet);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfEntity(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        int i = 0;
        for (Map.Entry<?, Collection<?>> entry : collectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<?> value = entry.getValue();
            String sheetName = Objects.toString(key);
            Class<?> clazz = this.getHeadClass(value);
            WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).head(clazz).build();
            excelWriter.write(value, writeSheet);
            i++;
        }
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfBase(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        int i = 0;
        for (Map.Entry<?, Collection<?>> entry : collectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<?> value = entry.getValue();
            String sheetName = Objects.toString(key);
            WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).build();
            Collection<Collection<?>> data = Collections.singletonList(value);
            excelWriter.write(data, writeSheet);
            i++;
        }
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfMap(ExcelExport excelExport, Map<?, Collection<Map<?, ?>>> collectionOfMapMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        int i = 0;
        for (Map.Entry<?, Collection<Map<?, ?>>> entry : collectionOfMapMap.entrySet()) {
            Object key = entry.getKey();
            Collection<Map<?, ?>> value = entry.getValue();
            String sheetName = Objects.toString(key);
            List<List<String>> head = this.getHeadList(value);
            WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).head(head).build();
            List<Map<Integer, Object>> data = value.stream().map(this::convert).collect(Collectors.toList());
            excelWriter.write(data, writeSheet);
            i++;
        }
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfCollection(ExcelExport excelExport, Map<?, Collection<Collection<?>>> collectionOfCollectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        ExcelWriter excelWriter = this.getExcelWriter(outputStream, excelExport);
        int i = 0;
        for (Map.Entry<?, Collection<Collection<?>>> entry : collectionOfCollectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<Collection<?>> value = entry.getValue();
            String sheetName = Objects.toString(key);
            WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).build();
            excelWriter.write(value, writeSheet);
            i++;
        }
        return outputStream;
    }

    private Class<?> getHeadClass(Collection<?> collection) {
        return collection.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(Object::getClass)
                .orElse(null);
    }

    private List<List<String>> getHeadList(Collection<Map<?, ?>> maps) {
        return maps.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::getHeadList)
                .orElseGet(Collections::emptyList);
    }

    private List<List<String>> getHeadList(Map<?, ?> map) {
        return map.keySet()
                .stream()
                .map(Objects::toString)
                .map(Collections::singletonList)
                .collect(Collectors.toList());
    }

    /**
     * 获取 {@link ExcelWriter}
     */
    private ExcelWriter getExcelWriter(OutputStream outputStream, ExcelExport excelExport) {
        ExcelTypeEnum excelTypeEnum;
        ExcelFileType excelFileType = excelExport.fileType();
        if (excelFileType == ExcelFileType.XLSX) {
            excelTypeEnum = ExcelTypeEnum.XLSX;
        } else if (excelFileType == ExcelFileType.XLS) {
            excelTypeEnum = ExcelTypeEnum.XLS;
        } else {
            throw new CommonException(String.format("Unsupported file type: %s", excelFileType));
        }
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream)
                .excelType(excelTypeEnum)
                .needHead(excelExport.withHeader())
                .inMemory(excelExport.inMemory())
                .automaticMergeHead(Boolean.TRUE)
                .autoCloseStream(Boolean.TRUE);
        String password = excelExport.password();
        if (StringUtils.hasText(password)) {
            excelWriterBuilder.password(password);
        }
        return excelWriterBuilder.build();
    }

    private Map<Integer, Object> convert(Map<?, ?> map) {
        Map<Integer, Object> dataMap = new LinkedHashMap<>(map.size(), 1.0F);
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            dataMap.put(i++, entry.getValue());
        }
        return dataMap;
    }
}
