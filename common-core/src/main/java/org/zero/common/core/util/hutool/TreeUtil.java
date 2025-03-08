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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 树型结构数据工具类
 **/
@UtilityClass
public class TreeUtil {
    /**
     * 获取指定同级树节点的最大深度
     *
     * @param treeList 同级树节点列表
     * @return 最大深度
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
    public static <T> long sumLeafNode(List<Tree<T>> treeList, long num) {
        if (CollUtil.isEmpty(treeList)) {
            return num;
        }

        for (Tree<T> node : treeList) {
            List<Tree<T>> children = node.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                num = sumLeafNode(children, num);
            } else {
                num += 1L;
            }
        }

        return num;
    }

    /**
     * 将表头列表转换为树状结构
     *
     * @param headers   表头列表
     * @param separator 表头分割符
     * @return 树状结构
     */
    public static List<Tree<String>> toTreeList(Collection<String> headers, String separator) {
        List<TreeNode<String>> treeNodes = toTreeNodeList(headers, separator);
        return cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
    }

    /**
     * 将表头列表转换为树状结构
     *
     * @param headers   表头列表
     * @param separator 表头分割符
     * @param cellStyle 单元格样式
     * @return 树状结构
     */
    public static List<Tree<String>> toTreeList(Collection<String> headers, String separator, CellStyle cellStyle) {
        List<TreeNode<String>> treeNodes = toTreeNodeList(headers, separator, cellStyle);
        return cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
    }

    /**
     * 将表头列表转换为树节点列表
     *
     * @param headers   表头列表
     * @param separator 表头分割符
     * @return 树节点列表
     */
    public static List<TreeNode<String>> toTreeNodeList(Collection<String> headers, String separator) {
        return toTreeNodeList(headers, separator, null);
    }

    /**
     * 将表头列表转换为树节点列表
     *
     * @param headers   表头列表
     * @param separator 表头分割符
     * @param cellStyle 单元格样式
     * @return 树节点列表
     */
    public static List<TreeNode<String>> toTreeNodeList(Collection<String> headers, String separator, CellStyle cellStyle) {
        List<List<String>> headersList = headers.stream()
                .map(h -> CharSequenceUtil.split(h, separator))
                .collect(Collectors.toList());
        Integer maxSize = headersList.stream()
                .map(List::size)
                .max(Comparator.naturalOrder())
                .orElse(1);
        return IntStream.rangeClosed(1, maxSize)
                .boxed()
                .flatMap(level -> headersList.stream()
                        .filter(hs -> hs.size() >= level)
                        .map(hs -> {
                            Map<String, Object> extraMap = MapUtil.<String, Object>builder()
                                    .put("cellStyle", cellStyle)
                                    .build();

                            String id = hs.stream()
                                    .limit(level)
                                    .collect(Collectors.joining(separator));
                            String parentId = null;
                            if (level > 1) {
                                parentId = hs.stream()
                                        .limit(level - 1L)
                                        .collect(Collectors.joining(separator));
                            }
                            String name = hs.get(level - 1);

                            return new TreeNode<>(id, parentId, name, null)
                                    .setExtra(extraMap);
                        })
                        .distinct())
                .collect(Collectors.toList());
    }
}
