package org.zero.common.core.support.bean.map;

import lombok.Getter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
@Getter
public class ObjectTree {
    protected Object object;
    protected Type type;
    protected String key;
    protected String name;
    protected long deep;
    protected long index;
    protected Kind kind;
    protected BeanProperty beanProperty;

    protected String levelSeparator;
    protected String multivariableLeft;
    protected String multivariableRight;

    protected ObjectTree parent;
    protected List<ObjectTree> children = new ArrayList<>();

    protected ObjectTree(Object object) {
        this.object = object;
    }

    protected static ObjectTree of(ObjectConfig objectConfig) {
        Object object = objectConfig.object;
        ObjectTree objectTree = new ObjectTree(object);
        objectTree.type = Objects.isNull(object) ? null : object.getClass();
        objectTree.key = objectConfig.prefix;
        objectTree.name = objectConfig.prefix;

        objectTree.levelSeparator = objectConfig.levelSeparator;
        objectTree.multivariableLeft = objectConfig.multivariableLeft;
        objectTree.multivariableRight = objectConfig.multivariableRight;
        return objectTree;
    }

    protected ObjectTree child(String name, Object object, Type type) {
        ObjectTree child = new ObjectTree(object);
        child.name = name;
        child.deep = deep + 1L;
        child.kind = Kind.OTHER;
        child.parent = this;
        child.type = Objects.isNull(object) ? type : object.getClass();
        child.key = Objects.isNull(key) ? name : String.format("%s%s%s", key, levelSeparator, name);
        this.copyInto(child);
        this.children.add(child);
        return child;
    }

    protected ObjectTree child(String name, Object object, BeanProperty beanProperty) {
        ObjectTree child = new ObjectTree(object);
        child.name = name;
        child.beanProperty = beanProperty;
        child.deep = deep + 1L;
        child.kind = Kind.OTHER;
        child.parent = this;
        child.type = Objects.isNull(object) ? beanProperty.getField().getGenericType() : object.getClass();
        child.key = Objects.isNull(key) ? name : String.format("%s%s%s", key, levelSeparator, name);
        this.copyInto(child);
        this.children.add(child);
        return child;
    }

    protected ObjectTree sibling(long index, Object object, Type type) {
        ObjectTree sibling = new ObjectTree(object);
        sibling.index = index;
        sibling.name = name;
        sibling.deep = deep;
        sibling.beanProperty = beanProperty;
        sibling.kind = Kind.MULTIVARIABLE_VALUE;
        sibling.parent = this.parent;
        sibling.type = Objects.isNull(object) ? type : object.getClass();
        sibling.key = String.format("%s%s%d%s", key, multivariableLeft, index, multivariableRight);
        this.copyInto(sibling);
        if (Objects.nonNull(this.parent)) {
            this.parent.children.add(sibling);
        }
        return sibling;
    }

    protected void copyInto(ObjectTree objectTree) {
        objectTree.levelSeparator = levelSeparator;
        objectTree.multivariableLeft = multivariableLeft;
        objectTree.multivariableRight = multivariableRight;
    }

    public enum Kind {
        MULTIVARIABLE_SELF,
        MULTIVARIABLE_VALUE,
        OTHER;
    }
}

