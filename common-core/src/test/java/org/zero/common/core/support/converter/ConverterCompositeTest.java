package org.zero.common.core.support.converter;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.TypeReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/23
 */
class ConverterCompositeTest {
    ConverterComposite converterComposite = ConverterComposite.getInstance();

    @Test
    void addConverter() {
        converterComposite.addConverter(new TypeReference<ArrayList<Integer>>() {
                }.getType(),
                new ToList<Integer>(ArrayList::new, Integer.class, converterComposite) {
                });
        converterComposite.addConverter(new TypeReference<Date[]>() {
                }.getType(),
                new ToArray<Date>(Date.class, converterComposite) {
                });
        converterComposite.converterMap.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    @Test
    void getConverters() {
        Collection<GenericConverter<?>> converters = converterComposite.getConverters(Number.class, false);
        for (GenericConverter<?> converter : converters) {
            System.out.println(converter);
        }
    }

    @Test
    void convertCollection() {
        addConverter();
        Object array = new String[]{"6767", "5", "554"};
        List<Integer> converted = converterComposite.convertExactAndQuietly(new TypeReference<ArrayList<Integer>>() {
        }.getType(), array);
        System.out.println(converted);
    }

    @Test
    void convert() {
        Number converted = converterComposite.convert(Number.class, "1", false, false);
        System.out.println(converted);
    }

    @Test
    void convertArray() {
        Object array = new String[]{"32443", "545", "65564"};
        // Object array = new int[]{43432, 44, 544};
        Integer[] converted = converterComposite.convertExactAndQuietly(Integer[].class, array);
        for (Integer integer : converted) {
            System.out.println(integer);
        }
    }
}