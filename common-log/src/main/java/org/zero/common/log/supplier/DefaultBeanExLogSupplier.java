package org.zero.common.log.supplier;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.log.util.LogUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author zero
 * @date 2022/1/3
 */
@Slf4j
public class DefaultBeanExLogSupplier implements LogSupplier {
    public static final LogSupplier INSTANCE = new DefaultBeanExLogSupplier();

    @Override
    public String getMessage(LogContext context) {
        Method method = context.getMethod();
        Object[] params = context.getParams();
        Parameter[] parameters = method.getParameters();
        int length = parameters.length;
        Map<String, Object> methodParamMap = MapUtil.newHashMap((int) (length / MapUtil.DEFAULT_LOAD_FACTOR));
        for (int i = 0; i < length; i++) {
            methodParamMap.put(parameters[i].getName(), params[i]);
        }
        return LogUtil.getMessage(context.getMessageTemplate(), methodParamMap);
    }
}