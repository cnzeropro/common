package org.zero.common.core.extension.jakarta.validation.constraints;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LandlineTest {
	@Test
	void messageDefaultsUseValidationMessageKey() throws NoSuchMethodException {
		assertEquals("{jakarta.validation.constraints.Landline.message}",
				Landline.class.getDeclaredMethod("message").getDefaultValue());
	}
}
