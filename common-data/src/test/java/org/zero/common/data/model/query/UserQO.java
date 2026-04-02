package org.zero.common.data.model.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.zero.common.data.model.persistent.UserPO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQO extends PageQO {
    private UserPO eq;
    private UserPO like;
    private UserMultiValueQO in;
    private UserRangeQO between;

    @Data
    public static class UserMultiValueQO implements Serializable {
        private Long[] ids = new Long[0];
        private String[] names = new String[0];
    }

    @Data
    public static class UserRangeQO implements Serializable {
        private Long startId;
        private Long endId;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startCreateTime;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endCreateTime;
    }
}
