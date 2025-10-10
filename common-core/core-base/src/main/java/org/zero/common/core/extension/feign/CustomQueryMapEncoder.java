package org.zero.common.core.extension.feign;

import feign.QueryMapEncoder;
import org.zero.common.core.util.BeanPathMapUtil;
import org.zero.common.core.util.hutool.core.bean.BeanUtil;

import java.util.Map;

/**
 * 用于 {@linkplain org.springframework.cloud.openfeign.SpringQueryMap SpringQueryMap} 注解：
 * bean 解析编码默认使用 {@linkplain feign.querymap.FieldQueryMapEncoder FieldQueryMapEncoder}，
 * 另外还有 {@linkplain feign.querymap.BeanQueryMapEncoder BeanQueryMapEncoder}，
 * 与 {@linkplain feign.querymap.FieldQueryMapEncoder FieldQueryMapEncoder} 不同的是：前者使用属性获取值，后者使用属性方法获取值（Getter）。
 * <p>
 * 但是两者会产生一些问题，比如不支持嵌套对象，日期时间格式化支持不足等等。因此自定义 {@link QueryMapEncoder}。
 * <p>
 * 但是还存在一个关键性致命问题：无法使用多个对象。要解决该问题，参见：
 * {@link org.zero.common.core.support.bean.query.QueryBeanEncoder}、
 * {@link org.zero.common.core.support.bean.query.QueryBean}、
 * {@link org.zero.common.core.support.bean.query.QueryBeanParameterProcessor}。
 *
 * @author zero
 * @since 2021/2/14
 */
public class CustomQueryMapEncoder implements QueryMapEncoder {
    protected String[] beanBasePackages = BeanUtil.DEFAULT_PACKAGE_LEVEL_NAMES;

    public CustomQueryMapEncoder(String... beanBasePackages) {
        this.beanBasePackages = beanBasePackages;
    }

    @Override
    public Map<String, Object> encode(Object object) {
        return BeanPathMapUtil.toMap(beanBasePackages, object);
    }
}
