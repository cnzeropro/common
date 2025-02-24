package org.zero.common.core.extension.java.converter;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/23
 */
class ConverterCompositeTest {
    ConverterComposite converterComposite = ConverterComposite.getInstance();

    @Test
    void convert() {
        // Integer i = converterComposite.convert(Integer.class, "1");
        // assertEquals(1, i);
        Object array = new int[]{43432, 44, 544};
        Integer[] convert = converterComposite.convertExactAndQuietly(Integer[].class, array);
        for (Integer integer : convert) {
            System.out.println(integer);
        }
    }

    @Test
    void addConverter() {
        System.out.println(Object.class.isAssignableFrom(int[].class));
        System.out.println(Number[].class.isAssignableFrom(int[].class));
        System.out.println(Number[].class.isAssignableFrom(Integer[].class));
    }

    @Test
    void testAddConverter() {
    }

    @Test
    void canConvert() {
    }
}