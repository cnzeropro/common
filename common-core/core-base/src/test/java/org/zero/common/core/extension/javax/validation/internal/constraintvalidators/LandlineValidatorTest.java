package org.zero.common.core.extension.javax.validation.internal.constraintvalidators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LandlineValidatorTest {
	@Test
	void emptyValuesAreValidForOptionalFields() {
		LandlineValidator validator = new LandlineValidator();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid("", null));
		assertTrue(validator.isValid(" ", null));
	}

	@Test
	void acceptsLandlineNumbers() {
		LandlineValidator validator = new LandlineValidator();

		assertTrue(validator.isValid("010-12345678", null));
	}

	@Test
	void rejectsNonLandlineNumbers() {
		LandlineValidator validator = new LandlineValidator();

		assertFalse(validator.isValid("13800138000", null));
		assertFalse(validator.isValid("not-a-phone", null));
	}
}
