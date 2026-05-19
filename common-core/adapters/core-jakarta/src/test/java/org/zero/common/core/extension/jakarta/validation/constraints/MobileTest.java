package org.zero.common.core.extension.jakarta.validation.constraints;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MobileTest {
	private static final String MESSAGE_KEY = "{jakarta.validation.constraints.Mobile.message}";

	@Test
	void messageDefaultsUseValidationMessageKey() throws NoSuchMethodException {
		assertEquals(MESSAGE_KEY, Mobile.class.getDeclaredMethod("message").getDefaultValue());
	}

	@Test
	void validationMessagesSupportLocales() {
		ResourceBundle zhBundle = ResourceBundle.getBundle("ValidationMessages", Locale.CHINA);
		ResourceBundle enBundle = ResourceBundle.getBundle("ValidationMessages", Locale.ENGLISH);

		assertEquals("手机号格式不正确",
				zhBundle.getString("jakarta.validation.constraints.Mobile.message"));
		assertEquals("Invalid mobile phone number format",
				enBundle.getString("jakarta.validation.constraints.Mobile.message"));
	}
}
