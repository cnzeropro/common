package org.zero.common.core.support.export.csv;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.FastByteArrayOutputStream;
import org.zero.common.core.support.export.BaseTabularDataExportResponseBodyAdvice;
import org.zero.common.core.support.export.FileExportEntity;
import org.zero.common.core.util.java.reflect.ClassUtil;
import org.zero.common.data.exception.CommonException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/11
 */
public abstract class BaseCsvResponseBodyAdvice extends BaseTabularDataExportResponseBodyAdvice {
    protected static final String NULL_DEFAULT = "";
    protected static final String LINE_BREAK = System.lineSeparator();

    protected BaseCsvResponseBodyAdvice(String... packageNames) {
        super(packageNames);
    }

    protected BaseCsvResponseBodyAdvice(ConfigurableConversionService conversionService, String... packageNames) {
        super(conversionService, packageNames);
    }

    protected BaseCsvResponseBodyAdvice(int order, String... packageNames) {
        super(order, packageNames);
    }

    protected BaseCsvResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String... packageNames) {
        super(conversionService, order, packageNames);
    }

    protected BaseCsvResponseBodyAdvice(String regex) {
        super(regex);
    }

    protected BaseCsvResponseBodyAdvice(ConfigurableConversionService conversionService, String regex) {
        super(conversionService, regex);
    }

    protected BaseCsvResponseBodyAdvice(int order, String regex) {
        super(order, regex);
    }

    protected BaseCsvResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String regex) {
        super(conversionService, order, regex);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(CsvExport.class);
    }

    @Override
    protected FileExportEntity export(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        CsvExport csvExport = Optional.ofNullable(returnType.getMethodAnnotation(CsvExport.class))
                // never goto here
                .orElseThrow(() -> new CommonException("@CsvExport annotation is null"));

        // handle collection data
        if (isCollection(body)) {
            Collection<?> collections = (Collection<?>) body;
            FastByteArrayOutputStream fastByteArrayOutputStream = this.handleCollection(csvExport, collections);
            return FileExportEntity.of(csvExport.filename(), fastByteArrayOutputStream, csvExport.fileType());
        }
        // handle map data
        if (isMap(body)) {
            Map<?, ?> map = (Map<?, ?>) body;
            FastByteArrayOutputStream fastByteArrayOutputStream = this.handleMap(csvExport, map);
            return FileExportEntity.of(csvExport.filename(), fastByteArrayOutputStream, csvExport.fileType());
        }

        throw new CommonException(String.format("@CsvExport annotation method does not support the return type: %s", body.getClass()));
    }

    /* ********************************************************* Collection Handler ********************************************************* */

    /**
     * handle like:
     * <ul>
     *    <li>List&lt;String&gt; (no head)</li>
     *    <li>List&lt;DataEntity&gt;</li>
     *    <li>List&lt;Map&lt;String, Object&gt;&gt;</li>
     *     <li>List&lt;List&lt;Long&gt;&gt; (no head)</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleCollection(CsvExport csvExport, Collection<?> collection) {
        // handle like: List<List<?>>
        if (isCollectionOfCollection(collection)) {
            @SuppressWarnings("unchecked")
            Collection<Collection<?>> collections = (Collection<Collection<?>>) collection;
            return this.handleCollectionOfCollection(csvExport, collections);
        }
        // handle like: List<Map<?, ?>>
        if (isCollectionOfMap(collection)) {
            @SuppressWarnings("unchecked")
            Collection<Map<?, ?>> maps = (Collection<Map<?, ?>>) collection;
            return this.handleCollectionOfMap(csvExport, maps);
        }
        // handle like: List<?>
        return this.handleCollectionOfOther(csvExport, collection);
    }

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Collection&lt;String&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfCollection(CsvExport csvExport, Collection<Collection<?>> collections);

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Map&lt;String, Byte&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfMap(CsvExport csvExport, Collection<Map<?, ?>> maps);


    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;DataEntity&gt;</li>
     *     <li>Collection&lt;String&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleCollectionOfOther(CsvExport csvExport, Collection<?> collection) {
        boolean hasEntity = collection.stream()
                .filter(Objects::nonNull)
                .map(Object::getClass)
                .allMatch(clazz -> ClassUtil.isClassWithRegexp(clazz, regex));
        if (hasEntity) {
            return this.handleCollectionOfEntity(csvExport, collection);
        } else {
            return this.handleCollectionOfBase(csvExport, collection);
        }
    }

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;DataEntity&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfEntity(CsvExport csvExport, Collection<?> collection);

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Integer&gt;</li>
     *     <li>Collection&lt;String&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfBase(CsvExport csvExport, Collection<?> collection);

    /* ********************************************************* Map Handler ********************************************************* */

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Object&gt;</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleMap(CsvExport csvExport, Map<?, ?> map) {
        // handle like: Map<?, ?>
        return this.handleMapOfOther(csvExport, map);
    }

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Number&gt;</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleMapOfOther(CsvExport csvExport, Map<?, ?> map);
}
