package org.zero.common.core.support.export.excel.poi;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zero.common.core.support.export.excel.BasePoiResponseBodyAdvice;
import org.zero.common.core.support.export.excel.ExcelExport;
import org.zero.common.core.support.export.excel.ExcelFileType;
import org.zero.common.core.util.java.reflect.FieldUtil;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
public class PoiResponseBodyAdvice extends BasePoiResponseBodyAdvice {
    public PoiResponseBodyAdvice(String... packageNames) {
        super(packageNames);
    }

    public PoiResponseBodyAdvice(int order, String... packageNames) {
        super(order, packageNames);
    }

    public PoiResponseBodyAdvice(String regex) {
        super(regex);
    }

    public PoiResponseBodyAdvice(int order, String regex) {
        super(order, regex);
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfEntity(ExcelExport excelExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        Sheet sheet = workbook.createSheet(sheetName);
        this.writeEntity(sheet, collection, excelExport.withHeader());
        this.setSheetPassword(sheet, excelExport.password());
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfBase(ExcelExport excelExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        Sheet sheet = workbook.createSheet(sheetName);
        Collection<Collection<?>> data = Collections.singletonList(collection);
        this.writeData(sheet, 0, data);
        this.setSheetPassword(sheet, excelExport.password());
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfEntity(ExcelExport excelExport, Collection<Collection<?>> collections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        int i = 0;
        for (Collection<?> collection : collections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            Sheet sheet = workbook.createSheet(sheetName);
            this.writeEntity(sheet, collection, excelExport.withHeader());
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfBase(ExcelExport excelExport, Collection<Collection<?>> collections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        int i = 0;
        for (Collection<?> collection : collections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            Sheet sheet = workbook.createSheet(sheetName);
            Collection<Collection<?>> data = Collections.singletonList(collection);
            this.writeData(sheet, 0, data);
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfMap(ExcelExport excelExport, Collection<Map<?, ?>> maps) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        Sheet sheet = workbook.createSheet(sheetName);
        this.writeMap(sheet, maps, excelExport.withHeader());
        this.setSheetPassword(sheet, excelExport.password());
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfCollection(ExcelExport excelExport, Collection<Collection<Collection<?>>> collectionOfCollections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        int i = 0;
        for (Collection<Collection<?>> collections : collectionOfCollections) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            Sheet sheet = workbook.createSheet(sheetName);
            this.writeData(sheet, 0, collections);
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfMap(ExcelExport excelExport, Collection<Collection<Map<?, ?>>> collectionOfMaps) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        int i = 0;
        for (Collection<Map<?, ?>> maps : collectionOfMaps) {
            String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, ++i);
            Sheet sheet = workbook.createSheet(sheetName);
            this.writeMap(sheet, maps, excelExport.withHeader());
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleMapOfOther(ExcelExport excelExport, Map<?, ?> map) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        String sheetName = String.format(SHEET_NAME_STYLE, SHEET_NAME_PREFIX, 1);
        Sheet sheet = workbook.createSheet(sheetName);
        Collection<Map<?, ?>> data = Collections.singletonList(map);
        this.writeMap(sheet, data, excelExport.withHeader());
        this.setSheetPassword(sheet, excelExport.password());
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfEntity(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        for (Map.Entry<?, Collection<?>> entry : collectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<?> value = entry.getValue();
            String sheetName = Objects.toString(key);
            Sheet sheet = workbook.createSheet(sheetName);
            this.writeEntity(sheet, value, excelExport.withHeader());
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfBase(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        for (Map.Entry<?, Collection<?>> entry : collectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<?> value = entry.getValue();
            String sheetName = Objects.toString(key);
            Sheet sheet = workbook.createSheet(sheetName);
            Collection<Collection<?>> data = Collections.singletonList(value);
            this.writeData(sheet, 0, data);
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfMap(ExcelExport excelExport, Map<?, Collection<Map<?, ?>>> collectionOfMapMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        for (Map.Entry<?, Collection<Map<?, ?>>> entry : collectionOfMapMap.entrySet()) {
            Object key = entry.getKey();
            Collection<Map<?, ?>> value = entry.getValue();
            String sheetName = Objects.toString(key);
            Sheet sheet = workbook.createSheet(sheetName);
            this.writeMap(sheet, value, excelExport.withHeader());
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleMapOfCollectionOfCollection(ExcelExport excelExport, Map<?, Collection<Collection<?>>> collectionOfCollectionMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Workbook workbook = this.getWorkbook(excelExport);
        for (Map.Entry<?, Collection<Collection<?>>> entry : collectionOfCollectionMap.entrySet()) {
            Object key = entry.getKey();
            Collection<Collection<?>> value = entry.getValue();
            String sheetName = Objects.toString(key);
            Sheet sheet = workbook.createSheet(sheetName);
            this.writeData(sheet, 0, value);
            this.setSheetPassword(sheet, excelExport.password());
        }
        workbook.write(outputStream);
        return outputStream;
    }

    private void writeEntity(Sheet sheet, Collection<?> entities, boolean withHeader) {
        Collection<Map<?, ?>> maps = entities.stream().map(this::getEntityMap).collect(Collectors.toList());
        this.writeMap(sheet, maps, withHeader);
    }

    private Map<String, Object> getEntityMap(Object entity) {
        List<Field> fields = FieldUtil.getAllFields(entity.getClass());
        return fields.stream().collect(Collectors.toMap(Field::getName, field -> FieldUtil.getFieldValue(field, entity)));
    }

    private void writeMap(Sheet sheet, Collection<Map<?, ?>> maps, boolean withHeader) {
        int rowIndex = 0;
        if (withHeader) {
            Collection<?> headers = maps.stream().filter(map -> !ObjectUtils.isEmpty(map)).findFirst().map(Map::keySet).orElseGet(Collections::emptySet);
            this.writeHeader(sheet, headers);
            rowIndex = 1;
        }
        Collection<Collection<?>> data = maps.stream().map(Map::values).collect(Collectors.toList());
        this.writeData(sheet, rowIndex, data);
    }

    private void writeHeader(Sheet sheet, Collection<?> headers) {
        Row row = sheet.createRow(0);
        int columnNum = 0;
        for (Object header : headers) {
            Cell cell = row.createCell(columnNum++);
            CellStyle cellStyle = this.createHeaderCellStyle(sheet.getWorkbook());
            cell.setCellStyle(cellStyle);
            cell.setCellValue(Objects.toString(header));
        }
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置前景色
        cellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.index);
        cellStyle.setFillPattern(FillPatternType.NO_FILL);
        // 设置边框
        IndexedColors indexedColors = IndexedColors.BLACK1;
        BorderStyle borderStyle = BorderStyle.MEDIUM;
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBottomBorderColor(indexedColors.index);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setLeftBorderColor(indexedColors.index);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setRightBorderColor(indexedColors.index);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setTopBorderColor(indexedColors.index);
        return cellStyle;
    }

    private void writeData(Sheet sheet, int startRowNum, Collection<Collection<?>> collections) {
        int maxColumnNum = 0;
        int rowNum = startRowNum;
        for (Collection<?> collection : collections) {
            Row row = sheet.createRow(rowNum++);
            int columnNum = 0;
            for (Object object : collection) {
                Cell cell = row.createCell(columnNum++);
                this.setCellValue(cell, object);
            }
            maxColumnNum = Math.max(maxColumnNum, columnNum);
        }
        this.autoSizeColumn(sheet, maxColumnNum);
    }

    private void autoSizeColumn(Sheet sheet, int maxColumnNum) {
        for (int i = 0; i < maxColumnNum; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void setCellValue(Cell cell, Object value) {
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(Objects.toString(value, ""));
        }
    }

    @SneakyThrows
    private Workbook getWorkbook(ExcelExport excelExport) {
        Workbook workbook;
        boolean xssf = ExcelFileType.XLSX.equals(excelExport.fileType());
        if (xssf) {
            if (excelExport.inMemory()) {
                workbook = WorkbookFactory.create(true);
            } else {
                workbook = new SXSSFWorkbook();
            }
        } else {
            workbook = WorkbookFactory.create(false);
        }

        this.setWorkbookPassword(workbook, excelExport.password());
        return workbook;
    }
}
