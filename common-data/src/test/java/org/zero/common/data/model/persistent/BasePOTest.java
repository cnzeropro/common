package org.zero.common.data.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
class BasePOTest {
	@Test
	void shouldBuildUserWithInheritedFields() {
		LocalDateTime createdAt = LocalDateTime.of(2025, 2, 14, 9, 30, 15);
		LocalDateTime updatedAt = createdAt.plusHours(6);
		UserPO user = buildUser(createdAt, updatedAt);

		assertAll(
			() -> assertEquals(Long.valueOf(1L), user.getId()),
			() -> assertEquals("Bob", user.getName()),
			() -> assertEquals(Long.valueOf(65474L), user.getCreatedBy()),
			() -> assertEquals(createdAt, user.getCreatedAt()),
			() -> assertEquals(Long.valueOf(91232234L), user.getUpdatedBy()),
			() -> assertEquals(updatedAt, user.getUpdatedAt()),
			() -> assertEquals(Boolean.FALSE, user.getDeleted()),
			() -> assertEquals(Long.valueOf(2L), user.getVersion()),
			() -> assertSame(Gender.MALE, user.getGender()),
			() -> assertSame(UserStatus.FREEZE, user.getStatus())
		);
	}

	@Test
	void shouldCreateModifiedCopyViaToBuilder() {
		LocalDateTime createdAt = LocalDateTime.of(2025, 2, 14, 9, 30, 15);
		LocalDateTime updatedAt = createdAt.plusHours(6);
		UserPO original = buildUser(createdAt, updatedAt);
		UserPO copied = original.toBuilder()
			.name("Alice")
			.version(3L)
			.build();

		assertAll(
			() -> assertNotSame(original, copied),
			() -> assertEquals("Bob", original.getName()),
			() -> assertEquals("Alice", copied.getName()),
			() -> assertEquals(original.getId(), copied.getId()),
			() -> assertEquals(original.getCreatedBy(), copied.getCreatedBy()),
			() -> assertEquals(original.getCreatedAt(), copied.getCreatedAt()),
			() -> assertEquals(Long.valueOf(2L), original.getVersion()),
			() -> assertEquals(Long.valueOf(3L), copied.getVersion())
		);
	}

	private UserPO buildUser(LocalDateTime createdAt, LocalDateTime updatedAt) {
		return UserPO.builder()
			.id(1L)
			.name("Bob")
			.createdBy(65474L)
			.gender(Gender.MALE)
			.status(UserStatus.FREEZE)
			.createdAt(createdAt)
			.updatedBy(91232234L)
			.updatedAt(updatedAt)
			.deleted(Boolean.FALSE)
			.version(2L)
			.build();
	}

	@Getter
	@RequiredArgsConstructor
	private enum Gender {
		MALE(1, "male"),
		FEMALE(2, "female"),
		UNKNOWN(0, "unknown");

		private final Integer type;
		private final String name;
	}

	@Getter
	@RequiredArgsConstructor
	private enum UserStatus {
		NORMAL(1, "normal"),
		LOCKED(2, "locked"),
		FREEZE(3, "freeze");

		private final Integer type;
		private final String name;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder(toBuilder = true)
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	private static class UserPO extends FullBasePO {
		private String code;
		private String name;
		private Gender gender;
		private UserStatus status;
	}
}
