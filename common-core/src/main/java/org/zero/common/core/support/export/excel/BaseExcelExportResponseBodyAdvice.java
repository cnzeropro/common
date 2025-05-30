package org.zero.common.core.support.export.excel;

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
import java.util.stream.Stream;

/**
 * 基础 excel 数据导出 ResponseBodyAdvice
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2022/5/23
 */
public abstract class BaseExcelExportResponseBodyAdvice extends BaseTabularDataExportResponseBodyAdvice {
    protected static final String SHEET_NAME_STYLE = "%s-%d";
    protected static final String SHEET_NAME_PREFIX = "Sheet";

    protected BaseExcelExportResponseBodyAdvice(String... packageNames) {
        super(packageNames);
    }

    protected BaseExcelExportResponseBodyAdvice(ConfigurableConversionService conversionService, String... packageNames) {
        super(conversionService, packageNames);
    }

    protected BaseExcelExportResponseBodyAdvice(int order, String... packageNames) {
        super(order, packageNames);
    }

    protected BaseExcelExportResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String... packageNames) {
        super(conversionService, order, packageNames);
    }

    protected BaseExcelExportResponseBodyAdvice(String regex) {
        super(regex);
    }

    protected BaseExcelExportResponseBodyAdvice(ConfigurableConversionService conversionService, String regex) {
        super(conversionService, regex);
    }

    protected BaseExcelExportResponseBodyAdvice(int order, String regex) {
        super(order, regex);
    }

    protected BaseExcelExportResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String regex) {
        super(conversionService, order, regex);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(ExcelExport.class);
    }

    @Override
    protected FileExportEntity export(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        ExcelExport excelExport = Optional.ofNullable(returnType.getMethodAnnotation(ExcelExport.class))
                // never goto here
                .orElseThrow(() -> new CommonException("@ExcelExport annotation is null"));
        // handle collection data
        if (isCollection(body)) {
            Collection<?> collections = (Collection<?>) body;
            FastByteArrayOutputStream fastByteArrayOutputStream = this.handleCollection(excelExport, collections);
            return FileExportEntity.of(excelExport.filename(), fastByteArrayOutputStream, excelExport.fileType());
        }
        // handle map data
        if (isMap(body)) {
            Map<?, ?> map = (Map<?, ?>) body;
            FastByteArrayOutputStream fastByteArrayOutputStream = this.handleMap(excelExport, map);
            return FileExportEntity.of(excelExport.filename(), fastByteArrayOutputStream, excelExport.fileType());
        }

        throw new CommonException(String.format("@ExcelExport annotation targetMethod does not support the return type: %s", body.getClass()));
    }

    /* ********************************************************* Collection Handler ********************************************************* */

    /**
     * handle like:
     * <ul>
     *     <li>One Sheet, One Row</li>
     *     <ul>
     *         <li>List&lt;String&gt; (no head)</li>
     *     </ul>
     *
     *     <li>One Sheet, Multiple Row</li>
     *     <ul>
     *         <li>List&lt;DataEntity&gt;</li>
     *         <li>List&lt;Map&lt;String, Object&gt;&gt;</li>
     *         <li>List&lt;List&lt;String&gt;&gt; (no head)</li>
     *     </ul>
     *
     *     <li>Multiple Sheet, Multiple Row</li>
     *     <ul>
     *         <li>List&lt;List&lt;DataEntity&gt;&gt;</li>
     *         <li>List&lt;List&lt;Map&lt;String, Object&gt;&gt;&gt;</li>
     *         <li>List&lt;List&lt;List&lt;String&gt;&gt;&gt; (no head)</li>
     *     </ul>
     * </ul>
     */
    protected FastByteArrayOutputStream handleCollection(ExcelExport excelExport, Collection<?> collection) {
        // handle like: List<List<?>>
        if (isCollectionOfCollection(collection)) {
            @SuppressWarnings("unchecked")
            Collection<Collection<?>> collections = (Collection<Collection<?>>) collection;
            return this.handleCollectionOfCollection(excelExport, collections);
        }
        // handle like: List<Map<?, ?>>
        if (isCollectionOfMap(collection)) {
            @SuppressWarnings("unchecked")
            Collection<Map<?, ?>> maps = (Collection<Map<?, ?>>) collection;
            return this.handleCollectionOfMap(excelExport, maps);
        }
        // handle like: List<?>
        return this.handleCollectionOfOther(excelExport, collection);
    }

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Collection&lt;String&gt;&gt;</li>
     *     <li>Collection&lt;Collection&lt;DataEntity&gt;&gt;</li>
     *     <li>Collection&lt;Collection&lt;Map&lt;String, Date&gt;&gt;&gt;</li>
     *     <li>Collection&lt;Collection&lt;Collection&lt;String&gt;&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleCollectionOfCollection(ExcelExport excelExport, Collection<Collection<?>> collections) {
        // handle like: List<List<List<?>>>
        if (isCollectionOfCollectionOfCollection(collections)) {
            @SuppressWarnings("unchecked")
            Collection<Collection<Collection<?>>> collectionOfCollections = (Collection<Collection<Collection<?>>>) (Object) collections;
            return this.handleCollectionOfCollectionOfCollection(excelExport, collectionOfCollections);
        }
        // handle like: List<List<Map<?, ?>>>
        if (isCollectionOfCollectionOfMap(collections)) {
            @SuppressWarnings("unchecked")
            Collection<Collection<Map<?, ?>>> collectionOfMaps = (Collection<Collection<Map<?, ?>>>) (Object) collections;
            return this.handleCollectionOfCollectionOfMap(excelExport, collectionOfMaps);
        }
        // handle like: List<List<?>>
        return this.handleCollectionOfCollectionOfOther(excelExport, collections);
    }


    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Collection&lt;Collection&lt;String&gt;&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfCollectionOfCollection(ExcelExport excelExport, Collection<Collection<Collection<?>>> collectionOfCollections);

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Collection&lt;Map&lt;String, Date&gt;&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfCollectionOfMap(ExcelExport excelExport, Collection<Collection<Map<?, ?>>> collectionOfMaps);

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Collection&lt;DataEntity&gt;&gt;</li>
     *     <li>Collection&lt;Collection&lt;Boolean&gt;&gt;</li>
     *     <li>Collection&lt;Collection&lt;String&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleCollectionOfCollectionOfOther(ExcelExport excelExport, Collection<Collection<?>> collections) {
        boolean hasEntity = collections.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .map(Object::getClass)
                .allMatch(clazz -> ClassUtil.isClassWithRegex(clazz, regex));
        if (hasEntity) {
            return this.handleCollectionOfCollectionOfEntity(excelExport, collections);
        } else {
            return this.handleCollectionOfCollectionOfBase(excelExport, collections);
        }
    }

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Collection&lt;DataEntity&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfCollectionOfEntity(ExcelExport excelExport, Collection<Collection<?>> collections);

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Collection&lt;String&gt;&gt;</li>
     *     <li>Collection&lt;Collection&lt;Double&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfCollectionOfBase(ExcelExport excelExport, Collection<Collection<?>> collections);

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;DataEntity&gt;</li>
     *     <li>Collection&lt;String&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleCollectionOfOther(ExcelExport excelExport, Collection<?> collection) {
        boolean hasEntity = collection.stream()
                .filter(Objects::nonNull)
                .map(Object::getClass)
                .allMatch(clazz -> ClassUtil.isClassWithRegex(clazz, regex));
        if (hasEntity) {
            return this.handleCollectionOfEntity(excelExport, collection);
        } else {
            return this.handleCollectionOfBase(excelExport, collection);
        }
    }

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;DataEntity&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfEntity(ExcelExport excelExport, Collection<?> collection);

    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Integer&gt;</li>
     *     <li>Collection&lt;String&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfBase(ExcelExport excelExport, Collection<?> collection);


    /**
     * handle like:
     * <ul>
     *     <li>Collection&lt;Map&lt;String, Byte&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleCollectionOfMap(ExcelExport excelExport, Collection<Map<?, ?>> maps);

    /* ********************************************************* Map Handler ********************************************************* */

    /**
     * handle like:
     * <ul>
     *     <li>One Sheet, One Row</li>
     *     <ul>
     *         <li>Map&lt;String, Object&gt;</li>
     *     </ul>
     *
     *     <li>Multiple Sheet, One Row</li>
     *     <ul>
     *         <li>Map&lt;String, Collection&lt;String&gt;&gt; (no head)</li>
     *     </ul>
     *
     *     <li>Multiple Sheet, Multiple Row</li>
     *     <ul>
     *         <li>Map&lt;String, Collection&lt;Collection&lt;String&gt;&gt;&gt; (no head)</li>
     *         <li>Map&lt;String, Collection&lt;Map&lt;String, Object&gt;&gt;&gt;</li>
     *         <li>Map&lt;String, Collection&lt;DataEntity&gt;&gt;</li>
     *     </ul>
     * </ul>
     */
    protected FastByteArrayOutputStream handleMap(ExcelExport excelExport, Map<?, ?> map) {
        // handle like: Map<?, List<?>>
        if (isMapOfCollection(map)) {
            @SuppressWarnings("unchecked")
            Map<?, Collection<?>> mapOfCollection = (Map<?, Collection<?>>) map;
            return this.handleMapOfCollection(excelExport, mapOfCollection);
        }
        // handle like: Map<?, ?>
        return this.handleMapOfOther(excelExport, map);
    }

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Collection&lt;String&gt;&gt;</li>
     *     <li>Map&lt;String, Collection&lt;DataEntity&gt;&gt;</li>
     *     <li>Map&lt;String, Collection&lt;DataEntity&gt;&gt;</li>
     *     <li>Map&lt;String, Collection&lt;Map&lt;String, Object&gt;&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleMapOfCollection(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        // handle like: Map<?, List<Map<?, ?>>>
        if (isMapOfCollectionOfMap(collectionMap)) {
            @SuppressWarnings("unchecked")
            Map<?, Collection<Map<?, ?>>> collectionOfMapMap = (Map<?, Collection<Map<?, ?>>>) (Object) collectionMap;
            return this.handleMapOfCollectionOfMap(excelExport, collectionOfMapMap);
        }
        // handle like: Map<?, List<List<?>>>
        if (isMapOfCollectionOfCollection(collectionMap)) {
            @SuppressWarnings("unchecked")
            Map<?, Collection<Collection<?>>> collectionOfCollectionMap = (Map<?, Collection<Collection<?>>>) (Object) collectionMap;
            return this.handleMapOfCollectionOfCollection(excelExport, collectionOfCollectionMap);
        }
        // handle like: Map<?, List<?>>
        return this.handleMapOfCollectionOfOther(excelExport, collectionMap);
    }

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Collection&lt;Map&lt;String, Float&gt;&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleMapOfCollectionOfMap(ExcelExport excelExport, Map<?, Collection<Map<?, ?>>> collectionOfMapMap);

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Collection&lt;Collection&lt;String&gt;&gt;&gt;</li>
     *     <li>Map&lt;String, Collection&lt;Collection&lt;Double&gt;&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleMapOfCollectionOfCollection(ExcelExport excelExport, Map<?, Collection<Collection<?>>> collectionOfCollectionMap);

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Collection&lt;DataEntity&gt;&gt;</li>
     *     <li>Map&lt;String, Collection&lt;Integer&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected FastByteArrayOutputStream handleMapOfCollectionOfOther(ExcelExport excelExport, Map<?, Collection<?>> collectionMap) {
        boolean hasEntity = collectionMap.values()
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .map(Object::getClass)
                .allMatch(clazz -> ClassUtil.isClassWithRegex(clazz, regex));
        if (hasEntity) {
            return this.handleMapOfCollectionOfEntity(excelExport, collectionMap);
        } else {
            return this.handleMapOfCollectionOfBase(excelExport, collectionMap);
        }
    }

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Collection&lt;DataEntity&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleMapOfCollectionOfEntity(ExcelExport excelExport, Map<?, Collection<?>> collectionMap);

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Collection&lt;String&gt;&gt;</li>
     *     <li>Map&lt;String, Collection&lt;BigDecimal&gt;&gt;</li>
     *     <li>Map&lt;String, Collection&lt;Integer&gt;&gt;</li>
     *     <li>...</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleMapOfCollectionOfBase(ExcelExport excelExport, Map<?, Collection<?>> collectionMap);

    /**
     * handle like:
     * <ul>
     *     <li>Map&lt;String, Number&gt;</li>
     * </ul>
     */
    protected abstract FastByteArrayOutputStream handleMapOfOther(ExcelExport excelExport, Map<?, ?> map);
}
