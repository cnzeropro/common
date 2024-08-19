package org.zero.common.core.util.hutool;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.junit.jupiter.api.Test;
import org.zero.common.data.exception.UtilException;

import java.util.ArrayList;
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
        List<TreeNode<String>> treeNodes = new ArrayList<>();
        treeNodes.add(new TreeNode<>("1", "0", "机构", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("8", "0", "机构1", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("9", "0", "机构2", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("10", "0", "机构3", Integer.MAX_VALUE));

        treeNodes.add(new TreeNode<>("2", "0", "代发企业", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("3", "2", "数量", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("4", "2", "较年初", Integer.MAX_VALUE));

        treeNodes.add(new TreeNode<>("5", "0", "代发个人客户", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("6", "5", "数量", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("7", "5", "较年初", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("11", "7", "较年初", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("12", "11", "较年初", Integer.MAX_VALUE));

        treeNodes.add(new TreeNode<>("13", "0", "基金", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("14", "13", "渗透率", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("15", "14", "渗透率1", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("16", "14", "渗透率2", Integer.MAX_VALUE));
        treeNodes.add(new TreeNode<>("17", "13", "本年提升率", Integer.MAX_VALUE));

        List<Tree<String>> trees = cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, "0");
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(String.format("C:\\Users\\Public\\Desktop\\%s.xlsx", IdUtil.fastSimpleUUID()));
        ExcelUtil.writeHeadAndDiagonalLine(writer, trees, 3);
        writer.write(ListUtil.of(ListUtil.of("a", "b", "c", 91, 54, 86, 5, 56, 3, 90, 14, 67, 66, 34),
                ListUtil.of("aa", "bb", "cc", 91, 54, 86, 5, 56, 3, 90, 14, 67, 66, 34),
                ListUtil.of("aaa", "bbb", "ccc", 91, 54, 86, 5, 56, 3, 90, 14, 67, 66, 34)));
        writer.flush();
        writer.close();
    }

    @Test
    void writeHead1() {
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
                .orElseThrow(() -> new UtilException("导出数据为空"));
        // 通过表头获取树节点列表
        List<TreeNode<String>> treeNodes = TreeUtil.toTreeNodeList(keys,".");
        // 调整顺序
        // treeNodes.forEach(treeNode -> {
        //     if (Objects.equals(treeNode.getId(), "c")) {
        //         treeNode.setWeight(1);
        //     }
        //     if (Objects.equals(treeNode.getId(), "d")) {
        //         treeNode.setWeight(2);
        //     }
        // });
        // 注意：表头顺序调整后，需要保证和数据顺序相同，否则表头与数据不匹配
        List<Tree<String>> trees = cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
        System.out.println(trees);

        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(String.format("C:\\Users\\Public\\Desktop\\%s.xlsx", IdUtil.fastSimpleUUID()));
        // 写入表头
        ExcelUtil.writeHead(writer, trees);
        // 写入数据
        writer.write(result, false);
        writer.flush();
        writer.close();
    }
}