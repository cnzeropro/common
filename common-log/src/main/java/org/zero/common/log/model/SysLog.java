package org.zero.common.log.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.zero.common.log.constant.LogType;
import org.zero.common.log.constant.OperateType;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zero
 * @since 2021/7/7
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysLog implements Serializable {
    /**
     * 日志内容
     */
    @NotBlank(message = "日志不能为空")
    private String message;

    /**
     * 日志类型
     */
    @NotBlank(message = "日志类型不能为空")
    private LogType type;

    /**
     * 操作类型
     */
    private OperateType operateType;

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
    private Boolean success;

    /**
     * 执行时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executionTime;

    /**
     * 持续时间（纳秒）
     */
    private Long costTime;
}
