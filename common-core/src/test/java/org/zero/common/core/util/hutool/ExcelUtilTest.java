package org.zero.common.core.util.hutool;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.jupiter.api.Test;
import org.zero.common.data.exception.UtilException;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // 获取表头
        Set<String> keys = result.stream()
                .findFirst()
                .map(Map::keySet)
                .orElseThrow(() -> new UtilException("Data header is empty"));

        // 通过表头获取树节点列表
        List<TreeNode<String>> treeNodes = TreeUtil.toTreeNodeList(keys, ".");

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
}