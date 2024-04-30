package org.zero.common.log.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.zero.common.log.supplier.DefaultLogSupplier;
import org.zero.common.log.supplier.LogContext;
import org.zero.common.log.supplier.LogSupplier;

/**
 * @author zero
 * @date 2022/1/3
 */
@Getter
@RequiredArgsConstructor
public enum LogMessageEngine {
    LOGIN(LogContext.builder()
            .messageTemplate("登录")
            .excludeParams(new String[]{"password"})
            .build(),
            DefaultLogSupplier.INSTANCE),
    REGISTER(LogContext.builder()
            .messageTemplate("注册")
            .excludeParams(new String[]{"password", "passwordConfirm"})
            .build(),
            DefaultLogSupplier.INSTANCE),
    CHANGE_PASSWORD(LogContext.builder()
            .messageTemplate("修改密码")
            .excludeParams(new String[]{"oldPassword", "newPassword", "newPasswordConfirm"})
            .build(),
            DefaultLogSupplier.INSTANCE),
    RESET_PASSWORD(LogContext.builder()
            .messageTemplate("重置密码")
            .excludeParams(new String[]{"newPassword", "newPasswordConfirm"})
            .build(),
            DefaultLogSupplier.INSTANCE),
    DEFAULT(LogContext.builder()
            .messageTemplate("执行方法")
            .build(),
            DefaultLogSupplier.INSTANCE),
    ;

    /**
     * 日志上下文
     */
    private final LogContext context;
    /**
     * 日志供给者（产生日志信息）
     */
    private final LogSupplier supplier;

    public LogContext getContext() {
        LogContext logContext = new LogContext();
        BeanUtils.copyProperties(context, logContext);
        return logContext;
    }
}
