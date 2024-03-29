package org.zero.common.core.extend.feign;

import feign.QueryMapEncoder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.zero.common.core.util.java.bean.BeanMapUtil;

import java.util.Map;

/**
 * 用于 @SpringQueryMap 注解：
 * bean 解析编码默认使用{@link feign.querymap.FieldQueryMapEncoder}，另外还有{@link feign.querymap.BeanQueryMapEncoder}，与FieldQueryMapEncoder不同的是：前者使用属性获取值，后者使用属性方法获取值（Getter）。
 * 但是两者会产生一些问题，比如不支持嵌套对象，日期时间格式化支持不足等等。因此自定义 QueryMapEncoder。
 * 但是还存在一个关键性致命问题：无法使用多个对象。
 *
 * @author zero
 * @since 2021/2/14
 */
@NoArgsConstructor
@AllArgsConstructor
public class CustomQueryMapEncoder implements QueryMapEncoder {
    protected String[] beanBasePackages = BeanMapUtil.DEFAULT_PACKAGE_NAMES;

    @Override
    public Map<String, Object> encode(Object object) {
        return BeanMapUtil.encodeIn(beanBasePackages, object);
    }
}
