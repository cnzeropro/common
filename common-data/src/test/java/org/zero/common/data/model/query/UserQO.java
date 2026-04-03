package org.zero.common.data.model.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.zero.common.data.enumeration.UserStatus;

import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQO extends PageQO {
	private String name;
	private UserStatus status;
	private Long[] ids = new Long[0];
	private Long startId;
	private Long endId;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdFrom;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdTo;
}
