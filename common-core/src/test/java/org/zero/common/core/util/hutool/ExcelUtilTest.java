package org.zero.common.core.util.hutool;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.jupiter.api.Test;
import org.zero.common.core.util.hutool.core.lang.tree.TreeUtil;
import org.zero.common.core.util.hutool.poi.excel.ExcelUtil;
import org.zero.common.data.exception.UtilException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zero
 * @since 2024/4/28
 */
class ExcelUtilTest {
    @Test
    void writeHead() {
        // 模拟数据
        List<Map<String, Object>> result = ListUtil.of(MapBuilder.<String, Object>create(true)
                        .put("a", 2)
                        .put("b.a", 4)
                        .put("b.b", 1)
                        .put("c.a.a", 62)
                        .put("c.a.b", 54)
                        .put("c.a.c", 43)
                        .put("c.b.a", 7)
                        .put("d.a.a", 5)
                        .build(),
                MapBuilder.<String, Object>create(true)
                        .put("a", 53)
                        .put("b.a", 34)
                        .put("b.b", 3)
                        .put("c.a.a", 4)
                        .put("c.a.b", 65)
                        .put("c.a.c", 9)
                        .put("c.b.a", 65)
                        .put("d.a.a", 2)
                        .build());

        // 获取原始表头
        Set<String> keys = result.stream()
                .findFirst()
                .map(Map::keySet)
                .orElseThrow(() -> new UtilException("Data header is empty"));

        // 创建表头树节点
        List<TreeNode<String>> treeNodes = TreeUtil.toTreeNodes(keys, ".");

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
        List<Tree<String>> trees = cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
        System.out.println(trees);

        String filename = String.format("C:\\Users\\Public\\Desktop\\%s.xlsx", IdUtil.fastSimpleUUID());
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(filename);
        // 写入表头
        ExcelUtil.writeHead(writer, trees);
        // 写入数据
        writer.write(result, false);
        writer.flush();
        writer.close();
    }

    @Test
    void writeDate() {
        // 模拟数据
        List<Map<String, String>> result = ListUtil.of(MapBuilder.<String, String>create(true)
                        .put("A", "a")
                        .put("B", "a")
                        .build(),
                MapBuilder.<String, String>create(true)
                        .put("A", "b.a")
                        .put("B", "b.a")
                        .build(),
                MapBuilder.<String, String>create(true)
                        .put("A", "b.b")
                        .put("B", "b.b")
                        .build(),
                MapBuilder.<String, String>create(true)
                        .put("A", "c.a.a")
                        .put("B", "c.a.a")
                        .build(),
                MapBuilder.<String, String>create(true)
                        .put("A", "c.b.a")
                        .put("B", "c.b.a")
                        .build(),
                MapBuilder.<String, String>create(true)
                        .put("A", "c.b.b")
                        .put("B", "c.b.b")
                        .build());
        // 获取原始表头
        Set<String> keys = result.stream()
                .findFirst()
                .map(Map::keySet)
                .orElseThrow(() -> new UtilException("Data header is empty"));
        // 构建表头与数据树映射
        Map<String, List<Tree<String>>> dataMap = keys.stream()
                .collect(Collectors.toMap(Function.identity(), key -> {
                    List<String> values = result.stream()
                            .map(map -> map.get(key))
                            .collect(Collectors.toList());
                    List<TreeNode<String>> treeNodes = TreeUtil.toTreeNodes(values, ".");
                    return cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
                }));
        // 获取表头与合并列映射
        Map<String, Long> headerCrossMap = MapUtil.map(dataMap, (k, v) -> TreeUtil.sameLevelMaxDepth(v));
        System.out.println(headerCrossMap);
        // 获取数据树
        List<List<Tree<String>>> data = ListUtil.list(false, dataMap.values());
        data.forEach(System.out::println);

        String filename = String.format("C:\\Users\\Rongan\\Desktop\\%s.xlsx", IdUtil.fastSimpleUUID());
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(filename);
        // 写入表头
        int columnIndex = 0;
        for (Map.Entry<String, Long> entry : headerCrossMap.entrySet()) {
            String header = entry.getKey();
            int mergeCross = entry.getValue().intValue();
            writer.merge(0, 0, columnIndex, columnIndex + mergeCross - 1, header, true);
            columnIndex += mergeCross;
        }
        // 写入数据
        ExcelUtil.writeData(writer, data, 1);
        writer.flush();
        writer.close();
    }
}