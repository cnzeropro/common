package org.zero.common.core.util.hutool.core.lang.tree;

import cn.hutool.core.map.MapUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/5
 */
public class TreeNode<T> extends cn.hutool.core.lang.tree.TreeNode<T> {
    public static <T> TreeNode<T> of(T id, T parentId, String name) {
        return new TreeNode<>(id, parentId, name, null);
    }

    public static <T> TreeNode<T> of(T id, T parentId, String name, Comparable<?> weight) {
        return new TreeNode<>(id, parentId, name, weight);
    }

    protected TreeNode(T id, T parentId, String name, Comparable<?> weight) {
        super(id, parentId, name, weight);
    }

    public TreeNode<T> putExtra(String key, Object value) {
        Map<String, Object> extra = super.getExtra();
        if (Objects.isNull(extra)) {
            extra = MapUtil.newHashMap(true);
            super.setExtra(extra);
        }
        extra.put(key, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return Objects.equals(getId(), treeNode.getId()) && Objects.equals(getParentId(), treeNode.getParentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParentId());
    }
}
