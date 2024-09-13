package org.zero.common.core.extension.export;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 基础表格数据导出 ResponseBodyAdvice
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2022/5/23
 */
public abstract class BaseTabularDataExportResponseBodyAdvice extends BaseExportResponseBodyAdvice {
    protected static final String[] DEFAULT_PACKAGE_NAMES = {"model", "entity", "domain", "pojo"};
    protected static final String DEFAULT_REGEX_TEMPLATE = "^(\\w+\\.)*(%s)(\\.\\w+)*$";
    protected static final String DEFAULT_REGEX = String.format(DEFAULT_REGEX_TEMPLATE, StringUtils.arrayToDelimitedString(DEFAULT_PACKAGE_NAMES, "|"));

    protected String regex = DEFAULT_REGEX;

    protected BaseTabularDataExportResponseBodyAdvice(String... packageNames) {
        super();
        if (!ObjectUtils.isEmpty(packageNames)) {
            this.regex = String.format(DEFAULT_REGEX_TEMPLATE, StringUtils.arrayToDelimitedString(packageNames, "|"));
        }
    }

    protected BaseTabularDataExportResponseBodyAdvice(int order, String... packageNames) {
        super(order);
        if (!ObjectUtils.isEmpty(packageNames)) {
            this.regex = String.format(DEFAULT_REGEX_TEMPLATE, StringUtils.arrayToDelimitedString(packageNames, "|"));
        }
    }

    protected BaseTabularDataExportResponseBodyAdvice(String regex) {
        super();
        this.regex = regex;
    }

    protected BaseTabularDataExportResponseBodyAdvice(int order, String regex) {
        super(order);
        this.regex = regex;
    }

    @Override
    protected FileExportEntity export(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return null;
    }

    /**
     * Collection&lt;Collection&lt;Map&lt;Object,Object&gt;&gt;&gt;
     */
    protected static boolean isCollectionOfCollectionOfMap(Object obj) {
        if (isCollection(obj)) {
            Collection<?> collections = (Collection<?>) obj;
            return isCollectionOfCollectionOfMap(collections);
        }
        return false;
    }

    /**
     * Collection&lt;Collection&lt;Map&lt;Object,Object&gt;&gt;&gt;
     */
    protected static boolean isCollectionOfCollectionOfMap(Collection<?> collection) {
        return allNonNullMatch(collection, BaseTabularDataExportResponseBodyAdvice::isCollectionOfMap);
    }

    /**
     * Collection&lt;Collection&lt;Collection&lt;Object&gt;&gt;&gt;
     */
    protected static boolean isCollectionOfCollectionOfCollection(Object obj) {
        if (isCollection(obj)) {
            Collection<?> collections = (Collection<?>) obj;
            return isCollectionOfCollectionOfCollection(collections);
        }
        return false;
    }

    /**
     * Collection&lt;Collection&lt;Collection&lt;Object&gt;&gt;&gt;
     */
    protected static boolean isCollectionOfCollectionOfCollection(Collection<?> collection) {
        return allNonNullMatch(collection, BaseTabularDataExportResponseBodyAdvice::isCollectionOfCollection);
    }


    /**
     * Collection&lt;Map&lt;Object,Object&gt;&gt;
     */
    protected static boolean isCollectionOfMap(Object obj) {
        if (isCollection(obj)) {
            Collection<?> collections = (Collection<?>) obj;
            return isCollectionOfMap(collections);
        }
        return false;
    }

    /**
     * Collection&lt;Map&lt;Object,Object&gt;&gt;
     */
    protected static boolean isCollectionOfMap(Collection<?> collection) {
        return allNonNullMatch(collection, BaseTabularDataExportResponseBodyAdvice::isMap);
    }

    /**
     * Collection&lt;Collection&lt;Object&gt;&gt;
     */
    protected static boolean isCollectionOfCollection(Object obj) {
        if (isCollection(obj)) {
            Collection<?> collections = (Collection<?>) obj;
            return isCollectionOfCollection(collections);
        }
        return false;
    }

    /**
     * Collection&lt;Collection&lt;Object&gt;&gt;
     */
    protected static boolean isCollectionOfCollection(Collection<?> collection) {
        return allNonNullMatch(collection, BaseTabularDataExportResponseBodyAdvice::isCollection);
    }

    /**
     * Collection&lt;Object&gt;
     */
    protected static boolean isCollection(Object obj) {
        return isExpectedType(obj, Collection.class);
    }

    /**
     * Map&lt;Object,List&lt;Map&lt;Object,Object&gt;&gt;&gt;
     */
    protected static boolean isMapOfCollectionOfMap(Object obj) {
        if (isMap(obj)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            return isMapOfCollectionOfMap(map);
        }
        return false;
    }

    /**
     * Map&lt;Object,List&lt;Map&lt;Object,Object&gt;&gt;&gt;
     */
    protected static boolean isMapOfCollectionOfMap(Map<?, ?> map) {
        return allNonNullMatch(map.values(), BaseTabularDataExportResponseBodyAdvice::isCollectionOfMap);
    }

    /**
     * Map&lt;Object,Collection&lt;Collection&lt;Object&gt;&gt;&gt;
     */
    protected static boolean isMapOfCollectionOfCollection(Object obj) {
        if (isMap(obj)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            return isMapOfCollectionOfCollection(map);
        }
        return false;
    }

    /**
     * Map&lt;Object,Collection&lt;Collection&lt;Object&gt;&gt;&gt;
     */
    protected static boolean isMapOfCollectionOfCollection(Map<?, ?> map) {
        return allNonNullMatch(map.values(), BaseTabularDataExportResponseBodyAdvice::isCollectionOfCollection);
    }

    /**
     * Map&lt;Object,Collection&lt;Object&gt;&gt;
     */
    protected static boolean isMapOfCollection(Object obj) {
        if (isMap(obj)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            return isMapOfCollection(map);
        }
        return false;
    }

    /**
     * Map&lt;Object,Collection&lt;Object&gt;&gt;
     */
    protected static boolean isMapOfCollection(Map<?, ?> map) {
        return allNonNullMatch(map.values(), BaseTabularDataExportResponseBodyAdvice::isCollection);
    }

    /**
     * Map&lt;Object,Object&gt;
     */
    protected static boolean isMap(Object obj) {
        return isExpectedType(obj, Map.class);
    }
}
