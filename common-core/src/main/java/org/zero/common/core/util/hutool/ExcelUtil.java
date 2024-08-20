package org.zero.common.core.util.hutool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.cell.CellUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;

import java.util.List;
import java.util.Objects;

/**
 * Excel 工具类
 **/
@Slf4j
@UtilityClass
public class ExcelUtil {
    /**
     * 写入 Excel 数据表头并绘制对角线
     *
     * @param writer Excel 写入器
     * @param trees  Excel 表头数据
     */
    public static <T> void writeHeadAndDiagonalLine(ExcelWriter writer, List<Tree<T>> trees) {
        writeHeadAndDiagonalLine(writer, trees, 1);
    }

    /**
     * 写入 Excel 数据表头并绘制对角线
     *
     * @param writer     Excel 写入器
     * @param trees      Excel 表头数据
     * @param passColumn 对角线覆盖的列数，从1开始
     * @param <T>        数据类型
     */
    public static <T> void writeHeadAndDiagonalLine(ExcelWriter writer, List<Tree<T>> trees, int passColumn) {
        long maxDepth = TreeUtil.sameLevelMaxDepth(trees);
        if (passColumn > 0) {
            writeCellDiagonalLine(writer, 0, 0, 0, 0, 0, 0, passColumn, (int) maxDepth);
        }
        writeHead(writer, trees, 0, passColumn);
    }

    /**
     * 写入 Excel 数据表头
     *
     * @param writer Excel 写入器
     * @param trees  Excel 表头数据
     * @param <T>    数据类型
     */
    public static <T> void writeHead(ExcelWriter writer, List<Tree<T>> trees) {
        writeHead(writer, trees, 0, 0);
    }

    /**
     * 写入 Excel 数据表头
     *
     * @param writer Excel 写入器
     * @param trees  Excel 表头数据
     * @param row    Excel 数据表头起始行，0开始
     * @param column Excel 数据表头起始列，0开始
     * @param <T>    数据类型
     */
    public static <T> void writeHead(ExcelWriter writer, List<Tree<T>> trees, int row, int column) {
        long maxDepth = TreeUtil.sameLevelMaxDepth(trees);
        writer.setCurrentRow(row);
        if (column > 0) {
            CellUtil.getOrCreateCell(writer.getOrCreateRow(row), column - 1);
        }
        writeHead0(writer, trees);
        writer.setCurrentRow(row + (int) maxDepth);
    }

    /**
     * 写入 Excel 数据表头
     *
     * @param writer Excel 写入器
     * @param trees  Excel 表头数据
     * @param <T>    数据类型
     */
    private static <T> void writeHead0(ExcelWriter writer, List<Tree<T>> trees) {
        if (CollUtil.isEmpty(trees)) {
            return;
        }
        for (Tree<T> node : trees) {
            // 起始行
            int firstRow = writer.getCurrentRow();
            int sameLevelMaxDepth = Math.max((int) TreeUtil.sameLevelMaxDepth(trees), 1);
            int maxDepth = (int) TreeUtil.maxDepth(node);
            // 合并行
            int mergeRow = sameLevelMaxDepth - maxDepth + 1;
            // 结束行
            int lastRow = firstRow + mergeRow - 1;

            // 起始列
            int firstColumn = Math.max(writer.getColumnCount(firstRow), 0);
            // 合并列
            long sumLeafNode = TreeUtil.sumLeafNode(node.getChildren(), 0L);
            int mergeColumn = Math.max((int) sumLeafNode, 1);
            // 结束列
            int lastColumn = firstColumn + mergeColumn - 1;

            // 绘制表头
            if (lastRow > firstRow || lastColumn > firstColumn) {
                // 单元格样式
                CellStyle cellStyle = (CellStyle) node.get("cellStyle");
                if (Objects.isNull(cellStyle)) {
                    writer.merge(firstRow, lastRow, firstColumn, lastColumn, node.getName(), true);
                } else {
                    writer.merge(firstRow, lastRow, firstColumn, lastColumn, node.getName(), cellStyle);
                }
            } else {
                writer.writeCellValue(firstColumn, firstRow, node.getName());
                // 单元格样式
                CellStyle cellStyle = (CellStyle) node.get("cellStyle");
                if (Objects.isNull(cellStyle)) {
                    writer.setStyle(writer.getHeadCellStyle(), firstColumn, firstRow);
                } else {
                    writer.setStyle(cellStyle, firstColumn, firstRow);
                }
            }

            List<Tree<T>> children = node.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                writer.setCurrentRow(firstRow + mergeRow);
                writeHead0(writer, children);
                writer.setCurrentRow(firstRow);
            }
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
                                             String cellContent) {
        writer.merge(0, row2 - 1, 0, col2 - 1, null, false);
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
            log.warn("Not supported this sheet drawing type: {}", drawingPatriarch.getClass());
        } else {
            log.warn("Unknown sheet drawing type: {}", drawingPatriarch.getClass());
        }

        // 写入对角线单元格内容
        if (CharSequenceUtil.isNotBlank(cellContent)) {
            writer.writeCellValue(0, 0, cellContent);
        }
    }
}
