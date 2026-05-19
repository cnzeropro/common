package org.zero.common.core.extension.javax.validation.constraints;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.spring.context.MessageSourceHelper;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MobileTest {
	private static final String MESSAGE_KEY = "{javax.validation.constraints.Mobile.message}";

	@Test
	void messageDefaultsUseValidationMessageKey() throws NoSuchMethodException {
		assertEquals(MESSAGE_KEY, Mobile.class.getDeclaredMethod("message").getDefaultValue());
	}

	@Test
	void validationMessagesSupportLocales() {
		MessageSourceHelper messageSource = MessageSourceHelper.of("ValidationMessages");

		assertEquals("手机号格式不正确",
				messageSource.getMessage("javax.validation.constraints.Mobile.message", null, Locale.CHINA));
		assertEquals("Invalid mobile phone number format",
				messageSource.getMessage("javax.validation.constraints.Mobile.message", null, Locale.ENGLISH));
	}
}
