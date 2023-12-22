package org.zero.common.log.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.zero.common.api.local.log.model.SysLogPO;
import org.zero.common.core.util.javax.net.IpUtil;
import org.zero.common.core.util.spring.RequestUtil;
import org.zero.common.core.util.spring.SpELUtil;
import org.zero.common.log.annotation.AutoLog;
import org.zero.common.log.event.SysLogEvent;
import org.zero.common.log.util.LogUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zero
 * @date 2022/1/3
 */
@Aspect
@Slf4j
public class AutoLogAspect implements InitializingBean {
    private ObjectMapper objectMapper;

    @Around("@annotation(autoLog)")
    public Object around(ProceedingJoinPoint joinPoint, AutoLog autoLog) throws Throwable {
        // 获取方法参数值
        Object[] args = joinPoint.getArgs();
        // 只会注解到方法上，所以直接安心强转
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法对象
        Method method = signature.getMethod();

        // 构建SysLog对象
        SysLogPO sysLog = SysLogPO.builder()
                .app(autoLog.app())
                .module(autoLog.module())
                .type(autoLog.type().name())
                .operateType(autoLog.operateType().name())
                .success(Boolean.TRUE)
                .build();
        long endTime = 0L;
        Object result = null;
        LocalDateTime startDateTime = LocalDateTime.now();
        long startTime = System.nanoTime();
        try {
            // 执行切面方法获取结果数据
            result = joinPoint.proceed();
            endTime = System.nanoTime();
        } catch (Throwable e) {
            endTime = System.nanoTime();
            // 异常时记录相关信息
            sysLog.setSuccess(Boolean.FALSE);
            sysLog.setException(e.getMessage());
            throw e;
        } finally {
            if (autoLog.withReturnData()) {
                sysLog.setReturnData(toExpectedStr(result));
            }
            sysLog.setExecutionTime(startDateTime);
            sysLog.setCostTime(endTime - startTime);

            // 获取指定SpEL表达式值
            String value = autoLog.value();
            try {
                value = SpELUtil.evaluate(method, args, value, String.class);
            } catch (Exception e) {
                log.warn(String.format("Parsing SpEL[%s] Error in @AutoLog", value), e);
            }
            sysLog.setMessage(value);

            // 封装请求相关信息
            RequestUtil.getHttpServletRequestOpt().ifPresent(request -> {
                sysLog.setRemoteAddr(IpUtil.getRemoteIp(request));
                sysLog.setRequestUri(URLUtil.getPath(request.getRequestURI()));
                sysLog.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
                sysLog.setRequestMethod(request.getMethod());
            });

            // 参数记录
            if (autoLog.withParam()) {
                String[] excludedParamNames = autoLog.excludedParamNames();
                Map<String, Object> paramMap = MapUtil.newHashMap();
                // 方法参数
                Map<String, Object> methodParam = getMethodParamInfo(method, args, excludedParamNames);
                paramMap.put("methodParam", methodParam);
                // 请求参数
                Map<String, Object> requestParam = getRequestParamInfo(excludedParamNames);
                paramMap.put("requestParam", requestParam);
                // 转化成json
                String param = objectMapper.writeValueAsString(paramMap);
                sysLog.setParam(param);
            }

            // todo: 调用iam服务api查询用户信息
            // String username = SpringSecurityUtil.getUsername();
            String username = "";
            sysLog.setOperator(username);

            // 发布事件
            SpringUtil.publishEvent(new SysLogEvent(sysLog));
        }

        return result;
    }

    /**
     * 封装方法的参数信息
     */
    private Map<String, Object> getMethodParamInfo(Method method, Object[] args, String[] excludedParamNames) {
        Map<String, Object> methodParamMap = MapUtil.newHashMap(true);
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String parameterName = parameters[i].getName();
            // 只记录未排除的参数
            if (!ArrayUtil.contains(excludedParamNames, parameterName)) {
                methodParamMap.put(parameterName, args[i]);
            }
        }
        return methodParamMap;
    }

    /**
     * 封装请求的参数信息
     */
    private Map<String, Object> getRequestParamInfo(String[] excludedParamNames) {
        Map<String, Object> resultMap = MapUtil.newHashMap();
        RequestUtil.getHttpServletRequestOpt().ifPresent(request -> {
            // 查询字符串（URL参数）（GET、DELETE等请求）
            String queryStr = LogUtil.excludeInQueryStr(request.getQueryString(), excludedParamNames);
            resultMap.put("queryStr", queryStr);
            // URL参数
            Map<String, List<String>> queryStrParam = HttpUtil.decodeParams(queryStr, StandardCharsets.UTF_8);
            resultMap.put("queryParam", queryStrParam);

            // 请求体参数（POST、PUT等请求），目前只处理了 application/json、multipart/form-data、application/x-www-form-urlencoded
            String contentType = request.getContentType();
            // application/x-www-form-urlencoded、multipart/form-data
            if (isFormUrlencoded(contentType) || isMultipart(contentType, false)) {
                Map<String, String[]> requestBodyParamMap = MapUtil.newHashMap(true);
                // 因为javax.servlet.ServletRequest.getParameterMap()方法会同时取到查询字符串和部分请求的请求体里的参数，所以需要排除查询字符串的参数
                Map<String, String[]> parameterMap = request.getParameterMap();
                Set<String> queryParamKeySet = queryStrParam.keySet();
                Set<String> parameterKeySet = parameterMap.keySet();
                // 交集
                Collection<String> intersection = CollUtil.intersection(queryParamKeySet, parameterKeySet);
                // 差集
                // Hutool工具提供的subtract()方法存在clone，会导致parameterKeySet拷贝报错
                // Collection<String> subtract = CollUtil.subtract(parameterKeySet, queryParamKeySet);
                Collection<String> difference = this.subtract(parameterKeySet, queryParamKeySet);
                // 共有参数处理
                intersection.forEach(s -> {
                    List<String> queryParamValues = queryStrParam.get(s);
                    String[] parameterValues = parameterMap.get(s);
                    List<String> requestBodyParams = ListUtil.list(true, parameterValues);
                    // 删除request parameter中的URL参数
                    // temp.removeAll(queryParamValues);
                    for (String queryParamValue : queryParamValues) {
                        requestBodyParams.remove(queryParamValue);
                    }
                    if (CollUtil.isNotEmpty(requestBodyParams)) {
                        requestBodyParamMap.put(s, requestBodyParams.toArray(new String[0]));
                    }
                });
                // 剩余参数处理
                difference.forEach(s -> requestBodyParamMap.put(s, parameterMap.get(s)));

                // 判断请求是否为文件上传类型（multipart/form-data）
                if (request instanceof MultipartHttpServletRequest) {
                    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                    // 获取文件参数并处理
                    Map<String, String[]> fileParamMap = multipartRequest.getMultiFileMap()
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                                    .stream()
                                    .map(MultipartFile::getOriginalFilename)
                                    .toArray(String[]::new)
                            ));
                    // 文件参数记录
                    requestBodyParamMap.putAll(fileParamMap);
                }

                // 排除指定参数
                Map<String, String[]> excludedRequestBodyParamMap = LogUtil.excludeInParamMap(requestBodyParamMap, excludedParamNames);
                resultMap.put("requestBody", excludedRequestBodyParamMap);
            }
            // application/json
            else if (isJson(contentType)) {
                String requestBody = readRequestBody(request);
                try {
                    JsonNode jsonNode = objectMapper.readTree(requestBody);
                    LogUtil.excludeInJson(jsonNode, excludedParamNames);
                    resultMap.put("requestBody", jsonNode);
                } catch (IOException e) {
                    // json解析报错后原样记录
                    log.warn("Failed to parse request body", e);
                    resultMap.put("requestBody", requestBody);
                }
            } else
            // 其他请求类型
            {
                resultMap.put("requestBody", readRequestBody(request));
            }
        });

        return resultMap;
    }

    private boolean isJson(String contentType) {
        return CharSequenceUtil.startWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE);
    }

    private boolean isFormUrlencoded(String contentType) {
        return CharSequenceUtil.startWithIgnoreCase(contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    private boolean isMultipart(String contentType, boolean strictServletCompliance) {
        return CharSequenceUtil.startWithIgnoreCase(contentType, strictServletCompliance ? MediaType.MULTIPART_FORM_DATA_VALUE : "multipart/");
    }

    private <T> Collection<T> subtract(Collection<T> coll1, Collection<T> coll2) {
        if (CollUtil.isNotEmpty(coll1) && CollUtil.isNotEmpty(coll2)) {
            Collection<T> result = new LinkedList<>(coll1);
            result.removeAll(coll2);
            return result;
        }
        return new ArrayList<>();
    }

    private String readRequestBody(HttpServletRequest request) {
        String requestBody = null;

        try {
            requestBody = IoUtil.read(request.getInputStream(), false).toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Failed to read RequestBody from ServletInputStream", e);
        }

        try {
            if (Objects.isNull(requestBody)) {
                requestBody = IoUtil.read(request.getReader(), false);
            }
        } catch (IOException e) {
            log.warn("Failed to read RequestBody from BufferedReader", e);
        }

        return requestBody;
    }

    private String toExpectedStr(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ignored) {
        }

        return obj.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 从容器中的对象copy而来，因为要进行配置调整，避免影响到全局
        objectMapper = SpringUtil.getBean(ObjectMapper.class).copy();
        // 序列化对象的所有属性，包括为Null的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 关闭 序列化时间日期为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 关闭 空对象报错（对应属性没有get方法）
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
