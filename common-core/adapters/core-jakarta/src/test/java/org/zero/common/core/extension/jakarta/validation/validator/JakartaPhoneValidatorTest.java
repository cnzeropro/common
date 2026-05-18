package org.zero.common.core.extension.jakarta.validation.validator;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JakartaPhoneValidatorTest {
	private static final String MOBILE_MESSAGE_KEY =
			"{jakarta.validation.validator.Mobile.message}";
	private static final String LANDLINE_MESSAGE_KEY =
			"{jakarta.validation.validator.Landline.message}";
	private static final String PHONE_MESSAGE_KEY =
			"{jakarta.validation.validator.Phone.message}";

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
		ResourceBundle zhBundle = ResourceBundle.getBundle("ValidationMessages", Locale.CHINA);
		ResourceBundle enBundle = ResourceBundle.getBundle("ValidationMessages", Locale.ENGLISH);

		assertEquals("手机号格式不正确",
				zhBundle.getString("jakarta.validation.validator.Mobile.message"));
		assertEquals("Invalid mobile phone number format",
				enBundle.getString("jakarta.validation.validator.Mobile.message"));
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
