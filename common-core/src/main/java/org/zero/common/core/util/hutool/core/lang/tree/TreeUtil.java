package org.zero.common.core.util.hutool.core.lang.tree;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.experimental.UtilityClass;
import org.zero.common.core.extension.java.util.function.TriFunction;

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
     * 获取指定树同级节点的最大深度
     *
     * @param nodes 树同级节点列表
     * @return 最大深度
     */
    public static <T> long sameLevelMaxDepth(List<Tree<T>> nodes) {
        long max = 0L;
        if (CollUtil.isEmpty(nodes)) {
            return max;
        }
        for (Tree<T> node : nodes) {
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
        long max = 0L;
        if (Objects.isNull(node)) {
            return max;
        }
        List<Tree<T>> children = node.getChildren();
        if (CollUtil.isNotEmpty(children)) {
            for (Tree<T> child : children) {
                long childDepth = maxDepth(child);
                max = Math.max(max, childDepth);
            }
        }
        return max + 1L;
    }

    /**
     * 获取指定树下所有叶子节点总数
     *
     * @param nodes 树节点列表
     * @return 叶子节点总数
     */
    public static <T> long sumLeafNode(List<Tree<T>> nodes) {
        long sum = 0L;
        if (CollUtil.isEmpty(nodes)) {
            return sum;
        }
        for (Tree<T> node : nodes) {
            List<Tree<T>> children = node.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                sum += sumLeafNode(children);
            } else {
                sum += 1L;
            }
        }
        return sum;
    }

    /**
     * 将表头列表转换为树状结构
     *
     * @param headers   表头列表
     * @param separator 表头分割符
     * @return 树状结构
     */
    public static List<Tree<String>> toTrees(Collection<String> headers, String separator) {
        List<TreeNode<String>> treeNodes = toTreeNodes(headers, separator);
        return cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
    }

    /**
     * 将表头列表转换为树状结构
     *
     * @param headers    表头列表
     * @param separator  表头分割符
     * @param nodeMapper 树节点映射器
     * @return 树状结构
     */
    public static List<Tree<String>> toTrees(Collection<String> headers, String separator, TriFunction<String, String, String, TreeNode<String>> nodeMapper) {
        List<TreeNode<String>> treeNodes = toTreeNodes(headers, separator, nodeMapper);
        return cn.hutool.core.lang.tree.TreeUtil.build(treeNodes, null);
    }

    /**
     * 将表头列表转换为树节点列表
     *
     * @param headers   表头列表
     * @param separator 表头分割符
     * @return 树节点列表
     */
    public static List<TreeNode<String>> toTreeNodes(Collection<String> headers, String separator) {
        return toTreeNodes(headers, separator, (id, parentId, name) -> new TreeNode<>(id, parentId, name, null));
    }

    /**
     * 将表头列表转换为树节点列表
     *
     * @param headers    表头列表
     * @param separator  表头分割符
     * @param nodeMapper 树节点映射器
     * @return 树节点列表
     */
    public static List<TreeNode<String>> toTreeNodes(Collection<String> headers, String separator, TriFunction<String, String, String, TreeNode<String>> nodeMapper) {
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
                            return nodeMapper.apply(id, parentId, name);
                        })
                        .distinct())
                .collect(Collectors.toList());
    }
}
