package org.zero.common.data.model.persistent;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.Gender;
import org.zero.common.data.enumeration.UserStatus;

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
}
