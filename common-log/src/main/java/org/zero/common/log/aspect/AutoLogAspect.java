package org.zero.common.log.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;
import org.zero.common.core.util.spring.context.SpringContextUtils;
import org.zero.common.log.annotation.AutoLog;
import org.zero.common.log.constant.LogMessageEngine;
import org.zero.common.log.constant.LogType;
import org.zero.common.log.constant.OperateType;
import org.zero.common.log.event.SysLogEvent;
import org.zero.common.log.model.SysLog;
import org.zero.common.log.supplier.LogContext;
import org.zero.common.log.supplier.LogSupplier;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author zero
 * @date 2022/1/3
 */
@Slf4j
@Aspect
public class AutoLogAspect {
    @Around("@annotation(autoLog)")
    public Object around(ProceedingJoinPoint joinPoint, AutoLog autoLog) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Object proxy = joinPoint.getThis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String app = autoLog.app();
        if (!StringUtils.hasText(app)) {
            app = SpringContextUtils.getAppName();
        }
        String module = autoLog.module();
        LogType logType = autoLog.type();
        OperateType operateType = autoLog.operateType();
        LogMessageEngine engine = autoLog.engine();
        LogSupplier supplier = engine.getSupplier();
        LogContext context = engine.getContext();
        context.setTarget(target)
                .setProxy(proxy)
                .setMethod(method)
                .setParams(args);

        SysLog sysLog = SysLog.builder()
                .app(app)
                .module(module)
                .type(logType)
                .operateType(operateType)
                .build();

        boolean before = autoLog.before();
        if (before) {
            this.setMessage(sysLog, supplier, context);
        }

        long endTime = 0L;
        Object result = null;
        LocalDateTime startDateTime = LocalDateTime.now();
        long startTime = System.nanoTime();
        try {
            result = joinPoint.proceed();
            endTime = System.nanoTime();
            sysLog.setSuccess(Boolean.TRUE);
        } catch (Throwable e) {
            endTime = System.nanoTime();
            // 异常时记录相关信息
            sysLog.setSuccess(Boolean.FALSE);
            context.setThrowable(e);
            throw e;
        } finally {
            context.setResult(result);
            if (!before) {
                this.setMessage(sysLog, supplier, context);
            }
            sysLog.setExecutionTime(startDateTime);
            sysLog.setCostTime(endTime - startTime);
            // todo: 调用 iam 服务 api 查询当前用户信息
            String username = "";
            sysLog.setOperator(username);
            // 发布事件
            SpringContextUtils.publishEvent(new SysLogEvent(sysLog));
        }

        return result;
    }

    private void setMessage(SysLog sysLog, LogSupplier supplier, LogContext context) {
        String message = null;
        try {
            message = supplier.getMessage(context);
        } catch (Exception e) {
            log.warn("Failed to obtain log message", e);
        }
        sysLog.setMessage(message);
    }
}
