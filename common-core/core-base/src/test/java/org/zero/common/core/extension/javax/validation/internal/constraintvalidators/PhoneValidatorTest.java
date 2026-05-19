package org.zero.common.core.extension.javax.validation.internal.constraintvalidators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneValidatorTest {
	@Test
	void emptyValuesAreValidForOptionalFields() {
		PhoneValidator validator = new PhoneValidator();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid("", null));
		assertTrue(validator.isValid("\t", null));
	}

	@Test
	void acceptsMobileAndLandlineNumbers() {
		PhoneValidator validator = new PhoneValidator();

		assertTrue(validator.isValid("13800138000", null));
		assertTrue(validator.isValid("010-12345678", null));
	}

	@Test
	void rejectsNonPhoneNumbers() {
		assertFalse(new PhoneValidator().isValid("not-a-phone", null));
	}
}
