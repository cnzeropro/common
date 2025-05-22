package org.zero.common.core.util.apache.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zero.common.core.util.java.lang.CharUtil;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/14
 */
public class ExcelUtil {
    public static final int MAX_COLUMN_CHAR = 255;
    public static final int DEFAULT_COLUMN_PIXEL = 256;
    public static final int MAX_COLUMN_WIDTH = MAX_COLUMN_CHAR * DEFAULT_COLUMN_PIXEL;
    public static final double CHAR_WIDTH_RATIO = 0.75;

    /**
     * 自动计算并设置列宽
     *
     * @param sheet Excel 工作表
     */
    public static void autoSizeColumn(Sheet sheet) {
        autoSizeColumn(sheet, DEFAULT_COLUMN_PIXEL);
    }

    /**
     * 自动计算并设置列宽
     *
     * @param sheet      Excel 工作表
     * @param extraWidth 额外边距（Excel 单位：字符宽度的 1/256）
     */
    public static void autoSizeColumn(Sheet sheet, int extraWidth) {
        Row row = sheet.getRow(0);
        if (Objects.isNull(row)) {
            return;
        }
        short columnCount = row.getLastCellNum();
        for (int i = 0; i < columnCount; i++) {
            autoSizeColumn(sheet, i, extraWidth);
        }
    }

    /**
     * 自动计算并设置列宽
     *
     * @param sheet      Excel 工作表
     * @param column     列索引（从 0 开始）
     * @param extraWidth 额外边距（Excel 单位：字符宽度的 1/256）
     */
    public static void autoSizeColumn(Sheet sheet, int column, int extraWidth) {
        int maxWidth = 0;
        for (Row row : sheet) {
            Cell cell = row.getCell(column);
            if (Objects.isNull(cell)) {
                continue;
            }
            int cellWidth = calculateCellWidth(cell);
            maxWidth = Math.max(maxWidth, cellWidth);
        }
        int width = Math.min(MAX_COLUMN_WIDTH, maxWidth + extraWidth);
        if (maxWidth > 0) {
            sheet.setColumnWidth(column, width);
        }
    }

    /**
     * 计算单元格宽度
     *
     * @param cell 单元格
     * @return 单元格宽度
     */
    public static int calculateCellWidth(Cell cell) {
        if (Objects.isNull(cell)) {
            return 0;
        }
        Sheet sheet = cell.getSheet();
        CellStyle cellStyle = cell.getCellStyle();
        Workbook workbook = sheet.getWorkbook();
        Font font = workbook.getFontAt(cellStyle.getFontIndex());
        int fontSize = font.getFontHeightInPoints();
        double baseWidth = fontSize * CHAR_WIDTH_RATIO;
        // 获取单元格内容
        String cellValue = cell.toString();
        // 处理换行符，分别计算每行宽度
        String[] lines = cellValue.split("\n");
        int maxWidth = 0;
        for (String line : lines) {
            double width = line.codePoints()
                    .map(c -> {
                        // 中文占两个字符
                        if (CharUtil.isChinese(c)) {
                            return 2;
                        }
                        // 其他字符占一个
                        return 1;
                    })
                    .map(i -> DEFAULT_COLUMN_PIXEL)
                    .mapToDouble(i -> i * baseWidth)
                    .sum();
            // 向上取整
            double ceiledWidth = Math.ceil(width);
            int lineWidth = ceiledWidth < MAX_COLUMN_WIDTH ? (int) ceiledWidth : MAX_COLUMN_WIDTH;
            maxWidth = Math.max(maxWidth, lineWidth);
        }
        return maxWidth;
    }

    protected ExcelUtil() {
        throw new UnsupportedOperationException();
    }
}
