package org.zero.common.data.model.persistant;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.Gender;
import org.zero.common.data.enumeration.UserStatus;

import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
class BasePOTest {
    @Test
    void test() {
        UserPO user = UserPO.builder()
                .id(1L)
                .name("Bob")
                .createdBy(65474L)
                .gender(Gender.MALE)
                .status(UserStatus.FREEZE)
                .createdAt(LocalDateTime.now())
                .updatedBy(91232234L)
                .updatedAt(LocalDateTime.now())
                .deleted(Boolean.FALSE)
                .version(2L)
                .build();
        System.out.println(user);
    }
}