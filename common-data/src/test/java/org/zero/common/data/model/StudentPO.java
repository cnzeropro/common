package org.zero.common.data.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.zero.common.data.constant.GenderEnum;

@Data
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("student")
public class StudentPO extends BasePO {
    private String sid;
    private String name;
    private GenderEnum gender;
}
