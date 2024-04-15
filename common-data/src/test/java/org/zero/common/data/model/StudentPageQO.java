package org.zero.common.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentPageQO extends PageQO<StudentPO, StudentQO.StudentMultiValueQO, StudentQO.StudentRangeQO> {
}
