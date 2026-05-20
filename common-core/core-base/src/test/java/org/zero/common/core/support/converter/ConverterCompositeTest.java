package org.zero.common.core.support.converter;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.lang.reflect.TypeReference;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/23
 */
class ConverterCompositeTest {
	@Test
	void addConverterShouldRegisterByDeclaredReturnType() {
		ConverterComposite converterComposite = new ConverterComposite();

		converterComposite.addConverter(ToInt.INSTANCE);

		assertTrue(converterComposite.canConvert(Integer.class, true));
	}

	@Test
	void convertShouldUseAssignableConverterWhenExactIsFalse() {
		ConverterComposite converterComposite = new ConverterComposite();
		converterComposite.addConverter(ToInt.INSTANCE);

		Number converted = converterComposite.convert(Number.class, "1", false, false);

		assertEquals(1, converted);
	}

	@Test
	void convertCollectionShouldUseRegisteredElementConverter() {
		ConverterComposite converterComposite = new ConverterComposite();
		converterComposite.addConverter(ToInt.INSTANCE);
		converterComposite.addConverter(new TypeReference<ArrayList<Integer>>() {
				}.getType(),
				new ToList<Integer>(ArrayList::new, Integer.class, converterComposite) {
				});

		List<Integer> converted = converterComposite.convertExactAndQuietly(new TypeReference<ArrayList<Integer>>() {
		}.getType(), new String[]{"6767", "5", "554"});

		assertEquals(java.util.Arrays.asList(6767, 5, 554), converted);
	}

	@Test
	void convertArrayShouldUseRegisteredElementConverter() {
		ConverterComposite converterComposite = new ConverterComposite();
		converterComposite.addConverter(ToInt.INSTANCE);
		converterComposite.addConverter(Integer[].class, new ToArray<Integer>(Integer.class, converterComposite) {
		});

		Integer[] converted = converterComposite.convertExactAndQuietly(
				Integer[].class,
				new String[]{"32443", "545", "65564"}
		);

		assertArrayEquals(new Integer[]{32443, 545, 65564}, converted);
	}
}
