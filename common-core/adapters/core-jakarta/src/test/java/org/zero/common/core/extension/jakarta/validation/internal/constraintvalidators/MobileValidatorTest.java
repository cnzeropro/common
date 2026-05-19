package org.zero.common.core.extension.jakarta.validation.internal.constraintvalidators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobileValidatorTest {
	@Test
	void emptyValuesAreValidForOptionalFields() {
		MobileValidator validator = new MobileValidator();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid("", null));
		assertTrue(validator.isValid(" ", null));
	}

	@Test
	void acceptsMobileNumbers() {
		MobileValidator validator = new MobileValidator();

		assertTrue(validator.isValid("13800138000", null));
		assertTrue(validator.isValid(new StringBuilder("13800138000"), null));
	}

	@Test
	void rejectsNonMobileNumbers() {
		MobileValidator validator = new MobileValidator();

		assertFalse(validator.isValid("010-12345678", null));
		assertFalse(validator.isValid("not-a-phone", null));
	}
}
