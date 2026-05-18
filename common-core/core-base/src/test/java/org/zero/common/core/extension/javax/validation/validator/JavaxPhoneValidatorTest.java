package org.zero.common.core.extension.javax.validation.validator;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.spring.context.MessageSourceHelper;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaxPhoneValidatorTest {
	private static final String MOBILE_MESSAGE_KEY =
			"{javax.validation.validator.Mobile.message}";
	private static final String LANDLINE_MESSAGE_KEY =
			"{javax.validation.validator.Landline.message}";
	private static final String PHONE_MESSAGE_KEY = "{javax.validation.validator.Phone.message}";

	@Test
	void nullValuesAreValidForOptionalFields() {
		assertTrue(new MobileValidator().isValid(null, null));
		assertTrue(new LandlineValidator().isValid(null, null));
		assertTrue(new PhoneValidator().isValid(null, null));
	}

	@Test
	void blankValuesAreValidForOptionalFields() {
		assertTrue(new MobileValidator().isValid("", null));
		assertTrue(new LandlineValidator().isValid(" ", null));
		assertTrue(new PhoneValidator().isValid("\t", null));
	}

	@Test
	void validatorsAcceptCharSequence() {
		assertTrue(new MobileValidator().isValid(new StringBuilder("13800138000"), null));
	}

	@Test
	void phoneIncludesMobileAndLandline() {
		assertTrue(new MobileValidator().isValid("13800138000", null));
		assertTrue(new LandlineValidator().isValid("010-12345678", null));
		assertTrue(new PhoneValidator().isValid("13800138000", null));
		assertTrue(new PhoneValidator().isValid("010-12345678", null));
		assertFalse(new PhoneValidator().isValid("not-a-phone", null));
	}

	@Test
	void messageDefaultsUseValidationMessageKeys() throws NoSuchFieldException {
		assertEquals(MOBILE_MESSAGE_KEY, Sample.class.getDeclaredField("mobile").getAnnotation(Mobile.class).message());
		assertEquals(LANDLINE_MESSAGE_KEY,
				Sample.class.getDeclaredField("landline").getAnnotation(Landline.class).message());
		assertEquals(PHONE_MESSAGE_KEY, Sample.class.getDeclaredField("phone").getAnnotation(Phone.class).message());
	}

	@Test
	void validationMessagesSupportLocales() {
		MessageSourceHelper messageSource = MessageSourceHelper.of("ValidationMessages");

		assertEquals("手机号格式不正确",
				messageSource.getMessage(
						"javax.validation.validator.Mobile.message",
						null,
						Locale.CHINA
				));
		assertEquals("Invalid mobile phone number format",
				messageSource.getMessage(
						"javax.validation.validator.Mobile.message",
						null,
						Locale.ENGLISH
				));
	}

	private static class Sample {
		@Mobile
		private CharSequence mobile;

		@Landline
		private CharSequence landline;

		@Phone
		private CharSequence phone;
	}
}
