package org.zero.common.core.support.export.excel.hutool;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zero.common.core.support.export.excel.BasePoiResponseBodyAdvice;
import org.zero.common.core.support.export.excel.ExcelExport;
import org.zero.common.core.support.export.excel.ExcelFileType;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/4
 */
@NoArgsConstructor
@ControllerAdvice
public class HutoolResponseBodyAdvice extends BasePoiResponseBodyAdvice {
    public HutoolResponseBodyAdvice(String... packageNames) {
        super(packageNames);
    }

    public HutoolResponseBodyAdvice(int order, String... packageNames) {
        super(order, packageNames);
    }

    public HutoolResponseBodyAdvice(String regex) {
        super(regex);
    }

    public HutoolResponseBodyAdvice(int order, String regex) {
        super(order, regex);
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfEntity(ExcelExport excelExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        excelWriter.setSheet(sheetName);
        excelWriter.write(collection, excelExport.withHeader());
        excelWriter.autoSizeColumnAll();
        this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfBase(ExcelExport excelExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        excelWriter.setSheet(sheetName);
        // 包装一下，让其写一行，而不是写一列
        Collection<?> data = Collections.singletonList(collection);
        excelWriter.write(data, false);
        excelWriter.autoSizeColumnAll();
        this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfEntity(ExcelExport excelExport, Collection<Collection<?>> collections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        int i = 0;
        for (Collection<?> collection : collections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            excelWriter.setSheet(sheetName);
            excelWriter.write(collection, excelExport.withHeader());
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfBase(ExcelExport excelExport, Collection<Collection<?>> collections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        int i = 0;
        for (Collection<?> collection : collections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            excelWriter.setSheet(sheetName);
            excelWriter.write(collection, false);
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfMap(ExcelExport excelExport, Collection<Map<?, ?>> maps) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        excelWriter.setSheet(sheetName);
        excelWriter.write(maps, excelExport.withHeader());
        excelWriter.autoSizeColumnAll();
        this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfCollection(ExcelExport excelExport, Collection<Collection<Collection<?>>> collectionOfCollections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        int i = 0;
        for (Collection<Collection<?>> collections : collectionOfCollections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            excelWriter.setSheet(sheetName);
            excelWriter.write(collections, false);
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfMap(ExcelExport excelExport, Collection<Collection<Map<?, ?>>> collectionOfMaps) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        int i = 0;
        for (Collection<Map<?, ?>> maps : collectionOfMaps) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            excelWriter.setSheet(sheetName);
            excelWriter.write(maps, excelExport.withHeader());
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfOther(ExcelExport excelExport, Map<?, ?> map) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        excelWriter.setSheet(sheetName);
        Collection<?> data = Collections.singletonList(map);
        excelWriter.write(data, excelExport.withHeader());
        excelWriter.autoSizeColumnAll();
        this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfEntity(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        for (Map.Entry<?, Collection<?>> entry : collectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<?> value = entry.getValue();
            String sheetName = Objects.toString(key);
            excelWriter.setSheet(sheetName);
            excelWriter.write(value, excelExport.withHeader());
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfBase(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        for (Map.Entry<?, Collection<?>> entry : collectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<?> value = entry.getValue();
            String sheetName = Objects.toString(key);
            excelWriter.setSheet(sheetName);
            Collection<Collection<?>> data = Collections.singletonList(value);
            excelWriter.write(data, false);
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfMap(ExcelExport excelExport, Map<?, Collection<Map<?, ?>>> collectionOfMapMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        for (Map.Entry<?, Collection<Map<?, ?>>> entry : collectionOfMapMap.entrySet()) {
            Object key = entry.getKey();
            Collection<Map<?, ?>> value = entry.getValue();
            String sheetName = Objects.toString(key);
            excelWriter.setSheet(sheetName);
            excelWriter.write(value, excelExport.withHeader());
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfCollection(ExcelExport excelExport, Map<?, Collection<Collection<?>>> collectionOfCollectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ExcelWriter excelWriter = this.getExcelWriter(excelExport);
        for (Map.Entry<?, Collection<Collection<?>>> entry : collectionOfCollectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<Collection<?>> value = entry.getValue();
            String sheetName = Objects.toString(key);
            excelWriter.setSheet(sheetName);
            excelWriter.write(value, false);
            excelWriter.autoSizeColumnAll();
            this.setSheetPassword(excelWriter.getSheet(), excelExport.password());
        }
        excelWriter.flush(outputStream);
        return outputStream;
    }

    private ExcelWriter getExcelWriter(ExcelExport excelExport) {
        ExcelWriter writer;
        boolean isXlsx = ExcelFileType.XLSX.equals(excelExport.fileType());
        if (isXlsx) {
            if (excelExport.inMemory()) {
                writer = ExcelUtil.getWriter(true);
            } else {
                writer = ExcelUtil.getBigWriter();
            }
        } else {
            writer = ExcelUtil.getWriter(false);
        }
        this.setWorkbookPassword(writer.getWorkbook(), excelExport.password());
        return writer;
    }
}
