package org.zero.common.core.support.export;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    /**
     * ConversionService conversionService = DefaultConversionService.getSharedInstance();
     */
    protected ConfigurableConversionService conversionService = new DefaultFormattingConversionService();

    /**
     * 判断是否是 pojo bean 的正则表达式。默认：包名称中带有 "model", "entity", "domain", "pojo" 的是 pojo bean
     */
    protected String regex;

    protected BaseTabularDataExportResponseBodyAdvice(String... packageNames) {
        super();
        this.regex = generateRegex(packageNames);
    }

    protected BaseTabularDataExportResponseBodyAdvice(ConfigurableConversionService conversionService, String... packageNames) {
        super();
        this.conversionService = conversionService;
        this.regex = generateRegex(packageNames);
    }

    protected BaseTabularDataExportResponseBodyAdvice(int order, String... packageNames) {
        super(order);
        this.regex = generateRegex(packageNames);
    }

    protected BaseTabularDataExportResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String... packageNames) {
        super(order);
        this.conversionService = conversionService;
        this.regex = generateRegex(packageNames);
    }

    protected BaseTabularDataExportResponseBodyAdvice(String regex) {
        super();
        this.regex = regex;
    }

    protected BaseTabularDataExportResponseBodyAdvice(ConfigurableConversionService conversionService, String regex) {
        super();
        this.conversionService = conversionService;
        this.regex = regex;
    }

    protected BaseTabularDataExportResponseBodyAdvice(int order, String regex) {
        super(order);
        this.regex = regex;
    }

    protected BaseTabularDataExportResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String regex) {
        super(order);
        this.conversionService = conversionService;
        this.regex = regex;
    }

    @Override
    protected FileExportEntity export(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return null;
    }

    protected String generateRegex(String... packageNames) {
        if (ObjectUtils.isEmpty(packageNames)) {
            return DEFAULT_REGEX;
        }
        return String.format(DEFAULT_REGEX_TEMPLATE, StringUtils.arrayToDelimitedString(packageNames, "|"));
    }

    protected <T> T convert(Object obj, Field field, Class<T> type) {
        if (Objects.isNull(obj)) {
            return null;
        }
        if (Objects.isNull(field)) {
            if (conversionService.canConvert(obj.getClass(), type)) {
                return conversionService.convert(obj, type);
            }
            // 转换不了的直接强转
            return type.cast(obj);
        }
        TypeDescriptor sourceType = new TypeDescriptor(field);
        TypeDescriptor targetType = new TypeDescriptor(ResolvableType.forClass(type), type, null);
        if (conversionService.canConvert(sourceType, targetType)) {
            Object converted = conversionService.convert(obj, sourceType, targetType);
            return type.cast(converted);
        }
        // 转换不了的直接强转
        return type.cast(obj);
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

    protected static <T> T getNonNullFirst(Collection<T> collection) {
        return getNonNullFirstOpt(collection).orElse(null);
    }

    protected static <T> Optional<T> getNonNullFirstOpt(Collection<T> collection) {
        return collection.stream().filter(Objects::nonNull).findFirst();
    }
}
