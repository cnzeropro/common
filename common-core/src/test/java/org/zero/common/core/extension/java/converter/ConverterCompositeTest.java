package org.zero.common.core.extension.java.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/23
 */
class ConverterCompositeTest {
    ConverterComposite converterComposite = ConverterComposite.getInstance();
    @Test
    void convert() {
        Integer i = converterComposite.convert(Integer.class, "1");
        assertEquals(1, i);
    }

    @Test
    void addConverter() {
    }

    @Test
    void testAddConverter() {
    }

    @Test
    void canConvert() {
    }
}