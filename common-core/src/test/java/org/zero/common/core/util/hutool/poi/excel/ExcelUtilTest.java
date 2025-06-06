package org.zero.common.core.util.hutool.poi.excel;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zero.common.core.util.hutool.core.lang.tree.TreeNode;
import org.zero.common.core.util.hutool.core.lang.tree.TreeUtil;
import org.zero.common.data.exception.UtilException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zero
 * @since 2024/4/28
 */
class ExcelUtilTest {
    ExcelWriter writer;
    List<Map<String, Object>> dataFrame;

    @Test
    void writeHeader() {
        // 获取原始表头
        Set<String> keys = dataFrame.stream()
                .findFirst()
                .map(Map::keySet)
                .orElseThrow(() -> new UtilException("Header is empty"));

        // 创建表头树节点
        List<TreeNode<CharSequence>> treeNodes = TreeUtil.toTreeNodes(keys, ".");

        // 调整顺序
        // 注意：表头顺序调整后，需要保证和数据顺序相同，否则表头与数据不匹配
        // treeNodes.forEach(treeNode -> {
        //     if (Objects.equals(treeNode.getId(), "c")) {
        //         treeNode.setWeight(1);
        //     }
        //     if (Objects.equals(treeNode.getId(), "d")) {
        //         treeNode.setWeight(2);
        //     }
        // });

        // 构建树
        List<Tree<CharSequence>> headers = TreeUtil.build(treeNodes, TreeUtil.DEFAULT_ROOT_ID);
        System.out.println("headers: ");
        headers.forEach(System.out::println);

        // 写入表头
        ExcelUtil.writeHeader(writer, headers);
        // 写入数据
        writer.write(dataFrame, false);
        writer.flush();

        int currentRow = writer.getCurrentRow();
        System.out.println("currentRow: " + currentRow);
    }

    @Test
    void writeData() {
        // 获取原始表头
        Set<String> keys = dataFrame.stream()
                .findFirst()
                .map(Map::keySet)
                .orElseThrow(() -> new UtilException("Header is empty"));
        // 构建表头与数据树映射
        Map<String, List<Tree<CharSequence>>> dataTreeMap = keys.stream()
                .collect(CollectorUtil.toMap(Function.identity(),
                        key -> {
                            List<Object> values = dataFrame.stream()
                                    .map(map -> map.get(key))
                                    .collect(Collectors.toList());
                            return TreeUtil.toTrees(values, ".");
                        },
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new));
        // 获取表头与合并列映射
        Map<String, Long> headerMergeMap = dataTreeMap.entrySet()
                .stream()
                .collect(CollectorUtil.toMap(Map.Entry::getKey,
                        entry -> {
                            List<Tree<CharSequence>> value = entry.getValue();
                            return TreeUtil.sameLevelMaxDepth(value);
                        },
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new));
        System.out.println("headerMergeMap: " + headerMergeMap);
        // 获取数据树
        List<List<Tree<CharSequence>>> data = ListUtil.list(false, dataTreeMap.values());
        System.out.println("data: ");
        data.forEach(System.out::println);

        // 写入表头
        int columnIndex = 0;
        for (Map.Entry<String, Long> entry : headerMergeMap.entrySet()) {
            String header = entry.getKey();
            int mergedCross = entry.getValue().intValue();
            if (mergedCross > 1) {
                writer.merge(0, 0, columnIndex, columnIndex + mergedCross - 1, header, true);
            } else {
                writer.writeCellValue(columnIndex, 0, header, true);
            }
            columnIndex += mergedCross;
        }
        // 写入数据
        ExcelUtil.writeData(writer, data, 1);
        writer.flush();

        int currentRow = writer.getCurrentRow();
        System.out.println("currentRow: " + currentRow);
    }

    @Test
    void write() {
        // 获取原始表头
        Set<String> keys = dataFrame.stream()
                .findFirst()
                .map(Map::keySet)
                .orElseThrow(() -> new UtilException("Data header is empty"));
        List<Tree<CharSequence>> headers = TreeUtil.toTrees(keys, ".");
        System.out.println("headers: ");
        headers.forEach(System.out::println);
        // 构建表头与数据树映射
        Map<String, List<Tree<CharSequence>>> dataTreeMap = keys.stream()
                .collect(CollectorUtil.toMap(Function.identity(),
                        key -> {
                            List<Object> values = dataFrame.stream()
                                    .map(map -> map.get(key))
                                    .collect(Collectors.toList());
                            return TreeUtil.toTrees(values, ".");
                        },
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new));
        // 获取表头与合并列映射
        Map<CharSequence, Long> headerMergeMap = dataTreeMap.entrySet()
                .stream()
                .collect(CollectorUtil.toMap(Map.Entry::getKey,
                        entry -> {
                            List<Tree<CharSequence>> value = entry.getValue();
                            return TreeUtil.sameLevelMaxDepth(value);
                        },
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new));
        System.out.println("headerMergeMap: " + headerMergeMap);
        // 设置表头节点跨列数
        headers.forEach(tree -> tree.walk(t -> {
            if (!t.hasChild()) {
                Optional.ofNullable(t.getId())
                        .map(headerMergeMap::get)
                        .ifPresent(mergedCross -> t.putExtra(ExcelUtil.MERGED_CROSS_COLUMN_KEY, mergedCross));
            }
        }));
        TreeUtil.dealMergedCross(headers);
        System.out.println("headers: ");
        headers.forEach(System.out::println);
        // 获取数据树
        List<List<Tree<CharSequence>>> data = ListUtil.list(false, dataTreeMap.values());
        System.out.println("data: ");
        data.forEach(System.out::println);

        // 写入表头
        ExcelUtil.writeHeader(writer, headers);
        int currentRow = writer.getCurrentRow();
        // 写入数据
        ExcelUtil.writeData(writer, data, currentRow);
        writer.flush();

        currentRow = writer.getCurrentRow();
        System.out.println("currentRow: " + currentRow);
    }

    @BeforeEach
    void beforeEach() {
        String filename = String.format("C:\\Users\\Rongan\\Desktop\\%s.xlsx", IdUtil.fastSimpleUUID());
        // String filename = String.format("C:\\Users\\Public\\Desktop\\%s.xlsx", IdUtil.fastSimpleUUID());
        writer = cn.hutool.poi.excel.ExcelUtil.getWriter(filename);

        // 模拟数据
        dataFrame = ListUtil.of(MapBuilder.<String, Object>create(true)
                        .put("A", 2)
                        .put("B", 4)
                        .put("C", 1)
                        .build(),
                MapBuilder.<String, Object>create(true)
                        .put("A", 53)
                        .put("B", 34)
                        .put("C", 3)
                        .build());

        // dataFrame = ListUtil.of(MapBuilder.<String, Object>create(true)
        //                 .put("a", 2)
        //                 .put("b.a", 4)
        //                 .put("b.b", 1)
        //                 .put("c.a.a", 62)
        //                 .put("c.a.b", 54)
        //                 .put("c.a.c", 43)
        //                 .put("c.b.a", 7)
        //                 .put("d.a.a", 5)
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("a", 53)
        //                 .put("b.a", 34)
        //                 .put("b.b", 3)
        //                 .put("c.a.a", 4)
        //                 .put("c.a.b", 65)
        //                 .put("c.a.c", 9)
        //                 .put("c.b.a", 65)
        //                 .put("d.a.a", 2)
        //                 .build());

        // dataFrame = ListUtil.of(MapBuilder.<String, Object>create(true)
        //                 .put("A", "a")
        //                 .put("B", "a")
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "b.a")
        //                 .put("B", "b.a")
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "b.b")
        //                 .put("B", "b.b")
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "c.a.a")
        //                 .put("B", "c.a.a")
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "c.b.a")
        //                 .put("B", "c.b.a")
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "c.b.b")
        //                 .put("B", "c.b.b")
        //                 .build());

        // dataFrame = ListUtil.of(MapBuilder.<String, Object>create(true)
        //                 .put("A", "a.a")
        //                 .put("B.A", 4)
        //                 .put("B.B", 1)
        //                 .put("C.A.A", 62)
        //                 .put("C.A.B", "x.x.x")
        //                 .put("C.A.C", 43)
        //                 .put("C.B.A", 7)
        //                 .put("D.A.A", 5)
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "a.b")
        //                 .put("B.A", 34)
        //                 .put("B.B", 3)
        //                 .put("C.A.A", 4)
        //                 .put("C.A.B", "x.x")
        //                 .put("C.A.C", 9)
        //                 .put("C.B.A", 65)
        //                 .put("D.A.A", 2)
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "a.c")
        //                 .put("B.A", 34)
        //                 .put("B.B", 3)
        //                 .put("C.A.A", 4)
        //                 .put("C.A.B", "x.y")
        //                 .put("C.A.C", 9)
        //                 .put("C.B.A", 65)
        //                 .put("D.A.A", 2)
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "b")
        //                 .put("B.A", 34)
        //                 .put("B.B", 3)
        //                 .put("C.A.A", 4)
        //                 .put("C.A.B", "y.x")
        //                 .put("C.A.C", 9)
        //                 .put("C.B.A", 767)
        //                 .put("D.A.A", 2)
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "c.a.a")
        //                 .put("B.A", 34)
        //                 .put("B.B", 3)
        //                 .put("C.A.A", 4)
        //                 .put("C.A.B", "z.x")
        //                 .put("C.A.C", 9)
        //                 .put("C.B.A", 564)
        //                 .put("D.A.A", 2)
        //                 .build(),
        //         MapBuilder.<String, Object>create(true)
        //                 .put("A", "c.a.b")
        //                 .put("B.A", 34)
        //                 .put("B.B", 765)
        //                 .put("C.A.A", 4)
        //                 .put("C.A.B", "z.x.x")
        //                 .put("C.A.C", 9)
        //                 .put("C.B.A", 65)
        //                 .put("D.A.A", 1)
        //                 .build());
    }

    @AfterEach
    void afterEach() {
        writer.close();
    }
}