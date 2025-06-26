package org.zero.common.core.support.export.csv;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;

import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/13
 */
@ControllerAdvice
public class CsvResponseBodyAdvice extends BaseCsvResponseBodyAdvice {
    public CsvResponseBodyAdvice(String... packageNames) {
        super(packageNames);
    }

    public CsvResponseBodyAdvice(ConfigurableConversionService conversionService, String... packageNames) {
        super(conversionService, packageNames);
    }

    public CsvResponseBodyAdvice(int order, String... packageNames) {
        super(order, packageNames);
    }

    public CsvResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String... packageNames) {
        super(conversionService, order, packageNames);
    }

    public CsvResponseBodyAdvice(String regex) {
        super(regex);
    }

    public CsvResponseBodyAdvice(ConfigurableConversionService conversionService, String regex) {
        super(conversionService, regex);
    }

    public CsvResponseBodyAdvice(int order, String regex) {
        super(order, regex);
    }

    public CsvResponseBodyAdvice(ConfigurableConversionService conversionService, int order, String regex) {
        super(conversionService, order, regex);
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfCollection(CsvExport csvExport, Collection<Collection<?>> collections) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup OutputStreamWriter outputStreamWriter = getOutputStreamWriter(outputStream, csvExport);

        // write value
        collections.forEach(collection -> {
            String value = collection.stream()
                    .map(this::convert)
                    .collect(Collectors.joining(csvExport.delimiter()));
            writeLine(outputStreamWriter, value);
        });

        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfMap(CsvExport csvExport, Collection<Map<?, ?>> maps) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup OutputStreamWriter outputStreamWriter = getOutputStreamWriter(outputStream, csvExport);
        String delimiter = csvExport.delimiter();

        // write header
        Map<?, ?> first = getNonNullFirst(maps);
        Map<Object, String> headerMap = getHeaderMap(first);
        if (csvExport.withHeader()) {
            String header = String.join(delimiter, headerMap.values());
            writeLine(outputStreamWriter, header);
        }

        // write value
        maps.forEach(map -> {
            String value = headerMap.keySet()
                    .stream()
                    .map(map::get)
                    .map(this::convert)
                    .collect(Collectors.joining(delimiter));
            writeLine(outputStreamWriter, value);
        });

        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfEntity(CsvExport csvExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup OutputStreamWriter outputStreamWriter = getOutputStreamWriter(outputStream, csvExport);
        String delimiter = csvExport.delimiter();

        // write header
        Object first = getNonNullFirst(collection);
        Map<Field, String> headerMap = getHeaderMap(first);
        if (csvExport.withHeader()) {
            String header = String.join(delimiter, headerMap.values());
            writeLine(outputStreamWriter, header);
        }

        // write value
        collection.forEach(obj -> {
            String value = headerMap.keySet()
                    .stream()
                    .map(field -> {
                        Object fieldValue = FieldUtil.getValue(field, obj);
                        return convert(fieldValue, field);
                    })
                    .collect(Collectors.joining(delimiter));
            writeLine(outputStreamWriter, value);
        });

        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleCollectionOfBase(CsvExport csvExport, Collection<?> collection) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup OutputStreamWriter outputStreamWriter = getOutputStreamWriter(outputStream, csvExport);

        // write value
        String value = collection.stream()
                .map(this::convert)
                .collect(Collectors.joining(csvExport.delimiter()));
        writeLine(outputStreamWriter, value);

        return outputStream;
    }

    @SneakyThrows
    @Override
    protected FastByteArrayOutputStream handleMapOfOther(CsvExport csvExport, Map<?, ?> map) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup OutputStreamWriter outputStreamWriter = getOutputStreamWriter(outputStream, csvExport);
        String delimiter = csvExport.delimiter();

        // write header
        Map<Object, String> headerMap = getHeaderMap(map);
        if (csvExport.withHeader()) {
            String header = String.join(delimiter, headerMap.values());
            writeLine(outputStreamWriter, header);
        }

        // write value
        String value = headerMap.keySet()
                .stream()
                .map(map::get)
                .map(this::convert)
                .collect(Collectors.joining(csvExport.delimiter()));
        writeLine(outputStreamWriter, value);

        return outputStream;
    }

    @SneakyThrows
    private OutputStreamWriter getOutputStreamWriter(FastByteArrayOutputStream outputStream, CsvExport csvExport) {
        String charset = csvExport.charset();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, charset);
        // write bom
        if (csvExport.withBom()) {
            String bom = ByteOrderMark.queryOpt(charset)
                    .map(ByteOrderMark::getBom)
                    .orElse(NULL_DEFAULT);
            outputStreamWriter.write(bom);
        }
        return outputStreamWriter;
    }

    private Map<Field, String> getHeaderMap(Object object) {
        return Optional.ofNullable(object)
                .map(Object::getClass)
                .map(Class::getDeclaredFields)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toMap(Function.identity(),
                        field -> {
                            if (field.isAnnotationPresent(HeaderAlias.class)) {
                                return field.getAnnotation(HeaderAlias.class).value();
                            } else {
                                return field.getName();
                            }
                        },
                        (oldVal, newVal) -> newVal,
                        LinkedHashMap::new));
    }

    private Map<Object, String> getHeaderMap(Map<?, ?> map) {
        return Optional.ofNullable(map)
                .map(Map::keySet)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toMap(Function.identity(),
                        Object::toString,
                        (oldVal, newVal) -> newVal,
                        LinkedHashMap::new));
    }

    private String convert(Object obj) {
        return convert(obj, null);
    }

    private String convert(Object obj, Field field) {
        try {
            String converted = convert(obj, field, String.class);
            return Objects.isNull(converted) ? NULL_DEFAULT : converted;
        } catch (Exception ignored) {
            return Objects.toString(obj, NULL_DEFAULT);
        }
    }

    @SneakyThrows
    private void writeLine(OutputStreamWriter outputStreamWriter, String content) {
        outputStreamWriter.write(content + LINE_BREAK);
    }
}
