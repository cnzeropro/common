package org.zero.common.core.support.bean.map;

import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public abstract class MapValueHandler<V> {
    protected final boolean ignoreNull;
    protected final boolean append;

    protected MapValueHandler(boolean ignoreNull, boolean append) {
        this.ignoreNull = ignoreNull;
        this.append = append;
    }

    protected void handle(ObjectTree objectTree, Map<String, V> map) {
        V convertedObject = this.convert(objectTree.object, objectTree.beanProperty);
        if (Objects.isNull(convertedObject) && this.ignoreNull){
            return;
        }
        this.addToMap(objectTree, map, convertedObject);
    }

    protected abstract V convert(Object object, BeanProperty beanProperty);

    protected abstract void addToMap(ObjectTree objectTree, Map<String, V> map, V value);
}
