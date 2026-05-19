package org.zero.common.core.extension.javax.validation.constraints;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhoneTest {
	@Test
	void messageDefaultsUseValidationMessageKey() throws NoSuchMethodException {
		assertEquals("{javax.validation.constraints.Phone.message}",
				Phone.class.getDeclaredMethod("message").getDefaultValue());
	}
}
