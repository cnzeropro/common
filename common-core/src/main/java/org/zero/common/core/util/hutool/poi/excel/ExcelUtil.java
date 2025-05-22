package org.zero.common.core.util.hutool.poi.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.java.Log;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.zero.common.core.util.hutool.core.lang.tree.TreeUtil;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static org.zero.common.core.util.apache.poi.ExcelUtil.DEFAULT_COLUMN_PIXEL;
import static org.zero.common.core.util.apache.poi.ExcelUtil.MAX_COLUMN_WIDTH;
import static org.zero.common.core.util.apache.poi.ExcelUtil.calculateCellWidth;

/**
 * Excel 工具类
 **/
@Log
public class ExcelUtil {
    public static final String CELL_STYLE_KEY = "cellStyle";
    public static final String MERGED_CROSS_COLUMN_KEY = "mergedCrossColumn";
    public static final String MERGED_CROSS_ROW_KEY = "mergedCrossRow";

    /**
     * 写入 Excel 数据表头并绘制对角线
     *
     * @param writer  Excel 写入器
     * @param headers 表头
     */
    public static <T> void writeDiagonalLineAndHeader(ExcelWriter writer, List<Tree<T>> headers) {
        writeDiagonalLineAndHeader(writer, headers, 1);
    }

    /**
     * 写入 Excel 数据表头并绘制对角线
     *
     * @param writer     Excel 写入器
     * @param headers    表头
     * @param passColumn 对角线覆盖的列数，从 1 开始
     * @param <T>        数据类型
     */
    public static <T> void writeDiagonalLineAndHeader(ExcelWriter writer, List<Tree<T>> headers, int passColumn) {
        long maxDepth = TreeUtil.sameLevelMaxDepth(headers);
        if (passColumn > 0) {
            writeCellDiagonalLine(writer, 0, 0, 0, 0, 0, 0, passColumn, (int) maxDepth);
        }
        writeHeader(writer, headers, 0, passColumn);
    }

    /**
     * 写入 Excel 表头
     *
     * @param writer  写入器
     * @param headers 表头
     * @param <T>     表头类型
     */
    public static <T> void writeHeader(ExcelWriter writer, List<Tree<T>> headers) {
        writeHeader(writer, headers, 0, 0);
    }

    /**
     * 写入 Excel 表头
     *
     * @param writer  Excel 写入器
     * @param headers 表头
     * @param row     表头起始行，从 0 开始
     * @param column  表头起始列，从 0 开始
     * @param <T>     表头类型
     */
    public static <T> void writeHeader(ExcelWriter writer, List<Tree<T>> headers, int row, int column) {
        long maxDepth = TreeUtil.sameLevelMaxDepth(headers);
        writer.setCurrentRow(row);
        writeHeader0(writer, headers, column);
        writer.setCurrentRow(row + (int) maxDepth);
    }

    protected static <T> void writeHeader0(ExcelWriter writer, List<Tree<T>> trees, int column) {
        if (CollUtil.isEmpty(trees)) {
            return;
        }
        for (Tree<T> node : trees) {
            // 起始行
            int firstRow = writer.getCurrentRow();
            // 合并行
            long sameLevelMaxDepth = TreeUtil.sameLevelMaxDepth(trees);
            long maxDepth = TreeUtil.maxDepth(node);
            Long mergedCrossRow = (Long) node.get(MERGED_CROSS_ROW_KEY);
            int mergedRow;
            if (Objects.isNull(mergedCrossRow)) {
                mergedRow = (int) (sameLevelMaxDepth - maxDepth);
            } else {
                mergedRow = mergedCrossRow.intValue() - 1;
            }
            // 结束行
            int lastRow = firstRow + mergedRow;

            // 起始列
            int firstColumn = Math.max(writer.getColumnCount(firstRow), column);
            // 合并列
            long sumLeafNode = TreeUtil.sumLeafNode(node.getChildren());
            Long mergedCrossColumn = (Long) node.get(MERGED_CROSS_COLUMN_KEY);
            int mergedColumn;
            if (Objects.isNull(mergedCrossColumn)) {
                mergedColumn = Math.max((int) sumLeafNode, 1) - 1;
            } else {
                mergedColumn = mergedCrossColumn.intValue() - 1;
            }
            // 结束列
            int lastColumn = firstColumn + mergedColumn;

            // 单元格样式
            CellStyle cellStyle = (CellStyle) node.get(CELL_STYLE_KEY);
            // 写入表头
            if (lastRow > firstRow || lastColumn > firstColumn) {
                if (Objects.isNull(cellStyle)) {
                    writer.merge(firstRow, lastRow, firstColumn, lastColumn, node.getName(), true);
                } else {
                    writer.merge(firstRow, lastRow, firstColumn, lastColumn, node.getName(), cellStyle);
                }
            } else {
                writer.writeCellValue(firstColumn, firstRow, node.getName());
                if (Objects.isNull(cellStyle)) {
                    writer.setStyle(writer.getHeadCellStyle(), firstColumn, firstRow);
                } else {
                    writer.setStyle(cellStyle, firstColumn, firstRow);
                }
            }

            List<Tree<T>> children = node.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                writer.setCurrentRow(lastRow + 1);
                writeHeader0(writer, children, column);
                writer.setCurrentRow(firstRow);
            }
        }
    }

    /**
     * 写入 Excel 数据（多行）
     *
     * @param writer Excel 写入器
     * @param data   Excel 数据（多行）
     * @param <T>    数据类型
     */
    public static <T> void writeData(ExcelWriter writer, List<List<Tree<T>>> data) {
        writeData(writer, data, 0);
    }

    /**
     * 写入 Excel 数据（多行）
     *
     * @param writer Excel 写入器
     * @param data   Excel 数据（多行）
     * @param row    Excel 数据起始行，从 0 开始
     * @param <T>    数据类型
     */
    public static <T> void writeData(ExcelWriter writer, List<List<Tree<T>>> data, int row) {
        writeData(writer, data, row, 0);
    }

    /**
     * 写入 Excel 数据（多行）
     *
     * @param writer Excel 写入器
     * @param data   Excel 数据（多行）
     * @param row    Excel 数据起始行，从 0 开始
     * @param column Excel 数据起始列，从 0 开始
     * @param <T>    数据类型
     */
    public static <T> void writeData(ExcelWriter writer, List<List<Tree<T>>> data, int row, int column) {
        int currentColumn = column;
        for (List<Tree<T>> datum : data) {
            writeDatum(writer, datum, row, currentColumn);
            currentColumn += (int) TreeUtil.sameLevelMaxDepth(datum);
        }
    }

    /**
     * 写入 Excel 数据（一行）
     *
     * @param writer Excel 写入器
     * @param datum  Excel 数据（一行）
     * @param <T>    数据类型
     */
    public static <T> void writeDatum(ExcelWriter writer, List<Tree<T>> datum) {
        writeDatum(writer, datum, 0, 0);
    }

    /**
     * 写入 Excel 数据（一行）
     *
     * @param writer Excel 写入器
     * @param datum  Excel 数据（一行）
     * @param row    Excel 数据起始行，从 0 开始
     * @param column Excel 数据起始列，从 0 开始
     * @param <T>    数据类型
     */
    public static <T> void writeDatum(ExcelWriter writer, List<Tree<T>> datum, int row, int column) {
        writer.setCurrentRow(row);
        writeDatum0(writer, datum, column);
        long sumLeafNode = TreeUtil.sumLeafNode(datum);
        writer.setCurrentRow(row + (int) sumLeafNode - 1);
    }

    protected static <T> void writeDatum0(ExcelWriter writer, List<Tree<T>> trees, int column) {
        if (CollUtil.isEmpty(trees)) {
            return;
        }
        for (Tree<T> node : trees) {
            // 起始行
            int firstRow = writer.getCurrentRow();
            // 合并行
            long sumLeafNode = TreeUtil.sumLeafNode(node.getChildren());
            Long mergedCrossRow = (Long) node.get(MERGED_CROSS_ROW_KEY);
            int mergedRow;
            if (Objects.isNull(mergedCrossRow)) {
                mergedRow = Math.max((int) sumLeafNode, 1) - 1;
            } else {
                mergedRow = mergedCrossRow.intValue() - 1;
            }
            // 结束行
            int lastRow = firstRow + mergedRow;

            // 起始列
            int firstColumn = Math.max(writer.getColumnCount(firstRow), column);
            // 合并列
            long sameLevelMaxDepth = TreeUtil.sameLevelMaxDepth(trees);
            long maxDepth = TreeUtil.maxDepth(node);
            Long mergedCrossColumn = (Long) node.get(MERGED_CROSS_COLUMN_KEY);
            int mergedColumn;
            if (Objects.isNull(mergedCrossColumn)) {
                mergedColumn = (int) (sameLevelMaxDepth - maxDepth);
            } else {
                mergedColumn = mergedCrossColumn.intValue() - 1;
            }
            // 结束列
            int lastColumn = firstColumn + mergedColumn;

            // 单元格样式
            CellStyle cellStyle = (CellStyle) node.get(CELL_STYLE_KEY);
            // 写入数据
            if (lastRow > firstRow || lastColumn > firstColumn) {
                if (Objects.isNull(cellStyle)) {
                    writer.merge(firstRow, lastRow, firstColumn, lastColumn, node.getName(), false);
                } else {
                    writer.merge(firstRow, lastRow, firstColumn, lastColumn, node.getName(), cellStyle);
                }
            } else {
                writer.writeCellValue(firstColumn, firstRow, node.getName());
                if (Objects.nonNull(cellStyle)) {
                    writer.setStyle(cellStyle, firstColumn, firstRow);
                }
            }

            List<Tree<T>> children = node.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                writeDatum0(writer, children, column);
            }
            writer.setCurrentRow(lastRow + 1);
        }
    }

    /**
     * 绘制单元格对角线
     *
     * @param writer Excel 写入器 {@link ExcelWriter}
     * @param dx1    第一个单元格内的 x 坐标
     * @param dy1    第一个单元格内的 y 坐标
     * @param dx2    第二个单元格内的 x 坐标
     * @param dy2    第二个单元格内的 y 坐标
     * @param col1   第一个单元格的列（基于 0）
     * @param row1   第一个单元格的行（基于 0）
     * @param col2   第二个单元格的列（基于 0）
     * @param row2   第二个单元格的行（基于 0）
     */
    public static void writeCellDiagonalLine(ExcelWriter writer,
                                             int dx1, int dy1,
                                             int dx2, int dy2,
                                             int col1, int row1,
                                             int col2, int row2) {
        writeCellDiagonalLine(writer, dx1, dy1, dx2, dy2, col1, row1, col2, row2, null);
    }

    /**
     * 绘制单元格对角线
     *
     * @param writer      Excel 写入器 {@link ExcelWriter}
     * @param dx1         第一个单元格内的 x 坐标
     * @param dy1         第一个单元格内的 y 坐标
     * @param dx2         第二个单元格内的 x 坐标
     * @param dy2         第二个单元格内的 y 坐标
     * @param col1        第一个单元格的列（基于 0）
     * @param row1        第一个单元格的行（基于 0）
     * @param col2        第二个单元格的列（基于 0）
     * @param row2        第二个单元格的行（基于 0）
     * @param cellContent 单元格内容
     */
    public static void writeCellDiagonalLine(ExcelWriter writer,
                                             int dx1, int dy1,
                                             int dx2, int dy2,
                                             int col1, int row1,
                                             int col2, int row2,
                                             Object cellContent) {
        writer.merge(0, row2 - 1, 0, col2 - 1, cellContent, false);
        Sheet sheet = writer.getSheet();
        Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
        if (drawingPatriarch instanceof XSSFDrawing) {
            XSSFSimpleShape shape = ((XSSFDrawing) drawingPatriarch).createSimpleShape(new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2));
            // 设置图形的类型为线
            shape.setShapeType(ShapeTypes.LINE);
            // 设置填充颜色
            shape.setFillColor(0, 0, 0);
            // 设置边框线型：solid=0、dot=1、dash=2、lgDash=3、dashDot=4、lgDashDot=5、lgDashDotDot=6、sysDash=7、sysDot=8、sysDashDot=9、sysDashDotDot=10
            shape.setLineStyle(0);
            // 设置边框线颜色
            shape.setLineStyleColor(0, 0, 0);
            // 设置边框线宽，单位：Point
            shape.setLineWidth(1);
        } else if (drawingPatriarch instanceof HSSFPatriarch) {
            HSSFSimpleShape shape = ((HSSFPatriarch) drawingPatriarch).createSimpleShape(new HSSFClientAnchor(dx1, dy1, dx2, dy2, (short) col1, row1, (short) col2, row2));
            // 设置图形的类型为线
            shape.setShapeType(ShapeTypes.LINE);
            // 设置填充颜色
            shape.setFillColor(0, 0, 0);
            // 设置边框线型：solid=0、dot=1、dash=2、lgDash=3、dashDot=4、lgDashDot=5、lgDashDotDot=6、sysDash=7、sysDot=8、sysDashDot=9、sysDashDotDot=10
            shape.setLineStyle(0);
            // 设置边框线颜色
            shape.setLineStyleColor(0, 0, 0);
            // 设置边框线宽，单位：Point
            shape.setLineWidth(1);
        } else if (drawingPatriarch instanceof SXSSFDrawing) {
            log.log(Level.WARNING, "Not supported this sheet drawing type: {0}", SXSSFDrawing.class);
        } else {
            log.log(Level.WARNING, "Unknown sheet drawing type: {0}", drawingPatriarch.getClass());
        }
    }

    /**
     * 单元格自适应宽度
     *
     * @param writer Excel 写入器 {@link ExcelWriter}
     */
    public static void autoSizeColumn(ExcelWriter writer) {
        autoSizeColumn(writer, DEFAULT_COLUMN_PIXEL);
    }

    /**
     * 单元格自适应宽度
     *
     * @param writer     Excel 写入器 {@link ExcelWriter}
     * @param extraWidth 额外宽度（Excel 单位：字符宽度的 1/256）
     */
    public static void autoSizeColumn(ExcelWriter writer, int extraWidth) {
        Sheet sheet = writer.getSheet();
        int columnCount = writer.getColumnCount();
        int physicalRowCount = writer.getPhysicalRowCount();
        for (int i = 0; i < columnCount; i++) {
            int maxWidth = 0;
            for (int j = 0; j < physicalRowCount; j++) {
                Cell cell = writer.getCell(i, j);
                int cellWidth = calculateCellWidth(cell);
                maxWidth = Math.max(maxWidth, cellWidth);
            }
            maxWidth += extraWidth;
            maxWidth = Math.min(maxWidth, MAX_COLUMN_WIDTH);
            if (maxWidth > 0) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }
    }

    protected ExcelUtil() {
        throw new UnsupportedOperationException();
    }
}
