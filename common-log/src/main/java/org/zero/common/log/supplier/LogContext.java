package org.zero.common.log.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Null;
import java.lang.reflect.Method;

/**
 * @author zero
 * @date 2022/1/3
 */
@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class LogContext {
    /**
     * 排除的参数
     */
    @Builder.Default
    private String[] excludeParams = new String[0];
    /**
     * 日志信息模板
     */
    @Builder.Default
    private String messageTemplate = "";

    /**
     * 目标对象的AOP代理对象
     */
    private Object proxy;
    /**
     * 目标对象
     */
    private Object target;
    /**
     * 目标对象方法
     */
    private Method method;
    /**
     * 方法参数
     */
    private Object[] params;
    /**
     * 方法返回结果
     */
    @Null
    private Object result;
    /**
     * 抛出的异常
     */
    @Null
    private Throwable throwable;
}
