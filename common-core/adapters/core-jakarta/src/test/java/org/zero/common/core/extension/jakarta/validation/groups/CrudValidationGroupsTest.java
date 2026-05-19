package org.zero.common.core.extension.jakarta.validation.groups;

import jakarta.validation.groups.Default;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrudValidationGroupsTest {
	@Test
	void scenarioGroupsAreMarkerInterfaces() {
		assertTrue(CrudValidationGroups.Create.class.isInterface());
		assertTrue(CrudValidationGroups.Read.class.isInterface());
		assertTrue(CrudValidationGroups.Update.class.isInterface());
		assertTrue(CrudValidationGroups.Delete.class.isInterface());
	}

	@Test
	void scenarioGroupsDoNotImplicitlyIncludeDefaultGroup() {
		assertFalse(Default.class.isAssignableFrom(CrudValidationGroups.Create.class));
		assertFalse(Default.class.isAssignableFrom(CrudValidationGroups.Read.class));
		assertFalse(Default.class.isAssignableFrom(CrudValidationGroups.Update.class));
		assertFalse(Default.class.isAssignableFrom(CrudValidationGroups.Delete.class));
	}
}
