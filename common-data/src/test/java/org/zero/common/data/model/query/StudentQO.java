package org.zero.common.data.model.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.zero.common.data.model.StudentPO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQO extends BaseQO {
    private StudentPO eq;
    private StudentPO like;
    private StudentMultiValueQO in;
    private StudentRangeQO between;

    @Data
    public static class StudentMultiValueQO implements Serializable {
        private Long[] ids = new Long[0];
        private String[] names = new String[0];
    }

    @Data
    public static class StudentRangeQO implements Serializable {
        private Long startId;
        private Long endId;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startCreateTime;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endCreateTime;
    }
}
