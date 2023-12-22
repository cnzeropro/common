package org.zero.common.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQO extends PageQO {
    private StudentPO student;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime beginCreateTime;

    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endCreateTime;
}
