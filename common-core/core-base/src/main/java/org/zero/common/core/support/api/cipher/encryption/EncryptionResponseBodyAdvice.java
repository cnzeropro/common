package org.zero.common.core.support.api.cipher.encryption;

import cn.hutool.core.lang.Opt;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zero.common.core.support.api.cipher.CodecProperties;
import org.zero.common.core.support.api.cipher.CodecUtil;
import org.zero.common.data.model.view.Result;

import javax.annotation.Resource;
import java.lang.reflect.Executable;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Slf4j
@EnableConfigurationProperties(CodecProperties.class)
@ControllerAdvice
public class EncryptionResponseBodyAdvice implements ResponseBodyAdvice<Result<Object>>, ApplicationContextAware {
    @Resource
    protected CodecProperties codecProperties;
    protected ApplicationContext applicationContext;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Result<Object> beforeBodyWrite(Result<Object> body, MethodParameter returnType, MediaType selectedContentType,
                                          Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                          ServerHttpRequest request, ServerHttpResponse response) {
        Executable executable = returnType.getExecutable();
        Encryption encryption = returnType.getMethodAnnotation(Encryption.class);
        Map<String, CodecProperties.CodecConfiguration> configMap = codecProperties.getEncryptionConfig();
        CodecProperties.CodecConfiguration config = CodecUtil.getConfig(executable, configMap);
        if ((Objects.isNull(encryption) && Objects.isNull(config)) ||
                (Objects.nonNull(encryption) && !encryption.enable())) {
            return body;
        }
        try {
            if (Objects.nonNull(body)) {
                Object data = body.getData();
                if (Objects.nonNull(data)) {
                    byte[] bytes = this.objectToBytes(data);
                    config = Opt.ofNullable(config)
                            .or(() -> Opt.ofNullable(configMap.get(codecProperties.getDefaultConfigKey())))
                            .orElseGet(CodecProperties.CodecConfiguration::new);
                    String encrypted = CodecUtil.encryptToStr(bytes, executable, encryption, config);
                    body.setData(encrypted);
                }
            }
            return body;
        } catch (Exception e) {
            log.warn(String.format("Result encrypt fail: %s", body), e);
            return Result.error("data encrypt fail");
        }
    }

    @SneakyThrows
    protected byte[] objectToBytes(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        ObjectMapper objectMapper = applicationContext.getBeanProvider(ObjectMapper.class).getIfAvailable(ObjectMapper::new);
        // 转换为 json 字节数组
        return objectMapper.writeValueAsBytes(obj);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
