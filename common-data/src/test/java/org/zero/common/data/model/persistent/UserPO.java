package org.zero.common.data.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.SuperBuilder;
import lombok.experimental.WithBy;
import org.zero.common.data.enumeration.Gender;
import org.zero.common.data.enumeration.UserStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@With
@WithBy
// fluent = true 可能会导致 jackson 等 json 工具序列化时出现错误结果（与具体版本有关）
// @Accessors(fluent = true, chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserPO extends FullBasePO {
    private String code;
    private String name;
    private Gender gender;
    private UserStatus status;
}
