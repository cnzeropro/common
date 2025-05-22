package org.zero.common.core.util.hutool.core.lang.tree;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeBuilder;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.parser.DefaultNodeParser;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.experimental.UtilityClass;
import org.zero.common.core.extension.java.util.function.TriFunction;
import org.zero.common.data.constant.StringPool;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.zero.common.core.util.hutool.poi.excel.ExcelUtil.MERGED_CROSS_COLUMN_KEY;

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
        if (node.hasChild()) {
            List<Tree<T>> children = node.getChildren();
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
            if (node.hasChild()) {
                List<Tree<T>> children = node.getChildren();
                sum += sumLeafNode(children);
            } else {
                sum += 1L;
            }
        }
        return sum;
    }

    public static <T> long dealMergedCross(List<Tree<T>> nodes) {
        long sumMergedCross = 0L;
        for (Tree<T> node : nodes) {
            if (node.hasChild()) {
                List<Tree<T>> children = node.getChildren();
                long mergedCross = dealMergedCross(children);
                node.putExtra(MERGED_CROSS_COLUMN_KEY, mergedCross);
                sumMergedCross += mergedCross;
            } else {
                Long mergedCross = (Long) node.get(MERGED_CROSS_COLUMN_KEY);
                sumMergedCross += Objects.isNull(mergedCross) ? 1L : mergedCross;
            }
        }
        return sumMergedCross;
    }

    public static final NodeParser<TreeNode<CharSequence>, CharSequence> DEFAULT_NODE_PARSER = new DefaultNodeParser<>();

    public static List<Tree<CharSequence>> build(List<TreeNode<CharSequence>> list, CharSequence rootId) {
        return build(list, rootId, TreeNodeConfig.DEFAULT_CONFIG, DEFAULT_NODE_PARSER);
    }

    public static List<Tree<CharSequence>> build(List<TreeNode<CharSequence>> list, CharSequence rootId, TreeNodeConfig treeNodeConfig, NodeParser<TreeNode<CharSequence>, CharSequence> nodeParser) {
        Map<TreeNode<CharSequence>, Integer> countMap = CollUtil.countMap(list);
        if (CollUtil.anyMatch(countMap.values(), count -> count > 1)) {
            return CollUtil.map(list,
                    treeNode -> {
                        Tree<CharSequence> tree = new Tree<>(treeNodeConfig);
                        nodeParser.parse(treeNode, tree);
                        return tree;
                    },
                    true);
        }
        return TreeBuilder.of(rootId, treeNodeConfig)
                .append(list, nodeParser)
                .build()
                .getChildren();
    }

    public static final String DEFAULT_ROOT_ID = StringPool.EMPTY;

    /**
     * 将数据列表转换为树状结构
     *
     * @param data      数据列表
     * @param separator 表头分割符
     * @return 树状结构
     */
    public static List<Tree<CharSequence>> toTrees(Collection<?> data, CharSequence separator) {
        return toTrees(data, separator, DEFAULT_ROOT_ID);
    }

    /**
     * 将数据列表转换为树状结构
     *
     * @param data      数据列表
     * @param separator 表头分割符
     * @param rootId    最顶层父 ID 值
     * @return 树状结构
     */
    public static List<Tree<CharSequence>> toTrees(Collection<?> data, CharSequence separator, CharSequence rootId) {
        List<TreeNode<CharSequence>> treeNodes = toTreeNodes(data, separator, rootId);
        return build(treeNodes, rootId);
    }

    /**
     * 将数据列表转换为树状结构
     *
     * @param data       数据列表
     * @param separator  表头分割符
     * @param nodeMapper 树节点映射器
     * @return 树状结构
     */
    public static List<Tree<CharSequence>> toTrees(Collection<?> data, CharSequence separator, TriFunction<CharSequence, CharSequence, String, TreeNode<CharSequence>> nodeMapper) {
        return toTrees(data, separator, DEFAULT_ROOT_ID, nodeMapper);
    }

    /**
     * 将数据列表转换为树状结构
     *
     * @param data       数据列表
     * @param separator  表头分割符
     * @param nodeMapper 树节点映射器
     * @param rootId     最顶层父 ID 值
     * @return 树状结构
     */
    public static List<Tree<CharSequence>> toTrees(Collection<?> data, CharSequence separator, CharSequence rootId, TriFunction<CharSequence, CharSequence, String, TreeNode<CharSequence>> nodeMapper) {
        List<TreeNode<CharSequence>> treeNodes = toTreeNodes(data, separator, rootId, nodeMapper);
        return build(treeNodes, rootId);
    }

    public static final TriFunction<CharSequence, CharSequence, String, TreeNode<CharSequence>> DEFAULT_NODE_MAPPER = (id, parentId, name) -> new TreeNode<>(id, parentId, name, null);

    /**
     * 将数据列表转换为树节点列表
     *
     * @param data      数据列表
     * @param separator 表头分割符
     * @return 树节点列表
     */
    public static List<TreeNode<CharSequence>> toTreeNodes(Collection<?> data, CharSequence separator) {
        return toTreeNodes(data, separator, DEFAULT_ROOT_ID);
    }

    /**
     * 将数据列表转换为树节点列表
     *
     * @param data      数据列表
     * @param separator 表头分割符
     * @param rootId    最顶层父 ID 值
     * @return 树节点列表
     */
    public static List<TreeNode<CharSequence>> toTreeNodes(Collection<?> data, CharSequence separator, CharSequence rootId) {
        return toTreeNodes(data, separator, rootId, DEFAULT_NODE_MAPPER);
    }

    /**
     * 将数据列表转换为树节点列表
     *
     * @param data       数据列表
     * @param separator  表头分割符
     * @param nodeMapper 树节点映射器
     * @return 树节点列表
     */
    public static List<TreeNode<CharSequence>> toTreeNodes(Collection<?> data, CharSequence separator, TriFunction<CharSequence, CharSequence, String, TreeNode<CharSequence>> nodeMapper) {
        return toTreeNodes(data, separator, DEFAULT_ROOT_ID, nodeMapper);
    }

    /**
     * 将数据列表转换为树节点列表
     *
     * @param data       数据列表
     * @param separator  分割符
     * @param nodeMapper 树节点映射器
     * @param rootId     最顶层父 ID 值
     * @return 树节点列表
     */
    public static List<TreeNode<CharSequence>> toTreeNodes(Collection<?> data, CharSequence separator, CharSequence rootId, TriFunction<CharSequence, CharSequence, String, TreeNode<CharSequence>> nodeMapper) {
        if (CollUtil.isEmpty(data)) {
            return Collections.emptyList();
        }
        List<List<String>> splitData = data.stream()
                .map(datum -> Objects.toString(datum, StringPool.EMPTY))
                .map(datum -> CharSequenceUtil.split(datum, separator))
                .collect(Collectors.toList());
        int maxSize = splitData.stream()
                .map(List::size)
                .max(Comparator.naturalOrder())
                .orElse(0);
        if (maxSize <= 1) {
            return splitData.stream()
                    .map(splitDatum -> {
                        String first = IterUtil.getFirst(splitDatum);
                        // don't inline
                        CharSequence id = first;
                        CharSequence parentId = rootId;
                        String name = first;
                        return nodeMapper.apply(id, parentId, name);
                    })
                    .collect(Collectors.toList());
        }
        return IntStream.rangeClosed(1, maxSize)
                .boxed()
                .flatMap(level -> splitData.stream()
                        .filter(splitDatum -> splitDatum.size() >= level)
                        .map(splitDatum -> {
                            CharSequence id = splitDatum.stream()
                                    .limit(level)
                                    .collect(Collectors.joining(separator));
                            CharSequence parentId = rootId;
                            if (level > 1) {
                                parentId = splitDatum.stream()
                                        .limit(level - 1L)
                                        .collect(Collectors.joining(separator));
                            }
                            String name = splitDatum.get(level - 1);
                            return nodeMapper.apply(id, parentId, name);
                        })
                        .distinct())
                .collect(Collectors.toList());
    }
}
