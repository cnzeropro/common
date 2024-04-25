package org.zero.common.api.local.log.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.zero.common.data.model.po.mp.BasePO;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author zero
 * @since 2021/7/7
 */
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class SysLogPO extends BasePO {
    /**
     * 日志内容
     */
    @NotBlank(message = "日志不能为空")
    private String message;

    /**
     * 日志类型
     */
    @NotBlank(message = "日志类型不能为空")
    private String type;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 日志所属项目
     */
    private String app;

    /**
     * 日志所属模块
     */
    private String module;

    /**
     * 是否是正常日志
     */
    @Getter(AccessLevel.NONE)
    private Boolean success;

    /**
     * 执行时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executionTime;

    /**
     * 持续时间（纳秒）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long costTime;

    public Boolean isSuccess() {
        return success;
    }
}
