package org.zero.common.data.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.SuperBuilder;
import org.zero.common.data.enumeration.GenderEnum;
import org.zero.common.data.enumeration.StatusEnum;

@Data
@SuperBuilder(toBuilder = true)
@With
// @WithBy
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("student")
public class StudentPO extends BasePO {
    private String code;
    private String name;
    private GenderEnum gender;
    private StatusEnum status;
}
