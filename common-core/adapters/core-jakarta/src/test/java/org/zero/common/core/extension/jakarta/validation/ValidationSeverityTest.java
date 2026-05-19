package org.zero.common.core.extension.jakarta.validation;

import jakarta.validation.Payload;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationSeverityTest {
	private static void assertFinalWithPrivateConstructor(Class<?> type) throws NoSuchMethodException {
		assertTrue(Modifier.isFinal(type.getModifiers()));
		Constructor<?> constructor = type.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test
	void severityTypesArePayloadMarkers() {
		assertTrue(Payload.class.isAssignableFrom(ValidationSeverity.Info.class));
		assertTrue(Payload.class.isAssignableFrom(ValidationSeverity.Warning.class));
		assertTrue(Payload.class.isAssignableFrom(ValidationSeverity.Error.class));
	}

	@Test
	void severityTypesAreFinalAndNonInstantiable() throws NoSuchMethodException {
		assertFinalWithPrivateConstructor(ValidationSeverity.Info.class);
		assertFinalWithPrivateConstructor(ValidationSeverity.Warning.class);
		assertFinalWithPrivateConstructor(ValidationSeverity.Error.class);
	}
}
