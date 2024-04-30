package org.zero.common.core.util.hutool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 树型结构数据工具类
 **/
@UtilityClass
public class TreeUtil {


    /**
     * 获取当前同级节点的最大叶子节点深度
     *
     * @param treeList 同级数节点列表
     * @return 同级节点中的最大叶子节点深度
     */
    public static <T> long sameLevelMaxDepth(List<Tree<T>> treeList) {
        long max = 0L;
        for (Tree<T> node : treeList) {
            max = Math.max(maxDepth(node), max);
        }
        return max;
    }

    /**
     * 获取指定树节点的最大深度
     *
     * @param node 树节点
     * @return 最大深度
     */
    public static <T> long maxDepth(Tree<T> node) {
        if (Objects.isNull(node)) {
            return 0L;
        }
        long maxChildDepth = 0L;
        List<Tree<T>> children = node.getChildren();
        if (CollUtil.isNotEmpty(children)) {
            for (Tree<T> child : children) {
                long childDepth = maxDepth(child);
                maxChildDepth = Math.max(maxChildDepth, childDepth);
            }
        }
        return maxChildDepth + 1L;
    }

    /**
     * 获取指定树下的所有叶子节点总数
     *
     * @param treeList 树节点列表
     * @param num      当前节点数
     * @return 叶子节点总数
     */
    public static <T> long leafNodeSum(List<Tree<T>> treeList, long num) {
        if (CollUtil.isEmpty(treeList)) {
            return num;
        }

        for (Tree<T> node : treeList) {
            List<Tree<T>> children = node.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                num = leafNodeSum(children, num);
            } else {
                num += 1L;
            }
        }

        return num;
    }

    public static List<Tree<String>> toTreeList(Collection<String> headers) {
        List<TreeNode<String>> treeNodes = toTreeNodeList(headers);
        return cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
    }

    public static List<Tree<String>> toTreeList(Collection<String> headers, CellStyle cellStyle) {
        List<TreeNode<String>> treeNodes = toTreeNodeList(headers, cellStyle);
        return cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
    }

    public static List<TreeNode<String>> toTreeNodeList(Collection<String> headers) {
        return toTreeNodeList(headers, null);
    }

    public static List<TreeNode<String>> toTreeNodeList(Collection<String> headers, CellStyle cellStyle) {
        List<List<String>> headersList = headers.stream()
                .map(h -> CharSequenceUtil.split(h, "."))
                .collect(Collectors.toList());
        return IntStream.rangeClosed(1, headersList.stream()
                        .map(List::size)
                        .max(Comparator.naturalOrder())
                        .orElse(1))
                .boxed()
                .flatMap(level -> headersList.stream()
                        .filter(hs -> hs.size() >= level)
                        .map(hs -> {
                            String id = hs.stream()
                                    .limit(level)
                                    .collect(Collectors.joining());
                            String parentId = null;
                            if (level > 1) {
                                parentId = hs.stream()
                                        .limit(level - 1L)
                                        .collect(Collectors.joining());
                            }
                            String name = hs.get(level - 1);

                            TreeNode<String> treeNode = new TreeNode<>(id, parentId, name, Integer.MAX_VALUE);
                            treeNode.setExtra(MapUtil.<String, Object>builder()
                                    .put("cellStyle", cellStyle)
                                    .build());
                            return treeNode;
                        })
                        .distinct())
                .collect(Collectors.toList());
    }
}
