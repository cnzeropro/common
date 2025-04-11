package org.zero.common.core.support.codec.decryption;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.zero.common.core.support.codec.CodecProperties;
import org.zero.common.core.support.codec.CodecUtil;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@EnableConfigurationProperties(CodecProperties.class)
@ControllerAdvice
public class DecryptionRequestBodyAdvice extends RequestBodyAdviceAdapter implements ApplicationContextAware {
    @Resource
    private CodecProperties codecProperties;
    private ApplicationContext applicationContext;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        Executable executable = parameter.getExecutable();
        Decryption decryption = parameter.getParameterAnnotation(Decryption.class);
        if (Objects.isNull(decryption)) {
            decryption = parameter.getMethodAnnotation(Decryption.class);
        }
        Map<String, CodecProperties.CodecConfiguration> configMap = codecProperties.getDecryptionConfig();
        CodecProperties.CodecConfiguration config = CodecUtil.getConfig(executable, configMap);
        if ((Objects.isNull(decryption) && Objects.isNull(config)) ||
                (Objects.nonNull(decryption) && !decryption.enable())) {
            return inputMessage;
        }

        String body = this.getBody(inputMessage);
        if (StrUtil.isNotEmpty(body)) {
            config = Opt.ofNullable(config)
                    .or(() -> Opt.ofNullable(configMap.get(codecProperties.getDefaultConfigKey())))
                    .orElseGet(CodecProperties.CodecConfiguration::new);
            byte[] decrypt = CodecUtil.decrypt(body, executable, decryption, config);
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() throws IOException {
                    return new ByteArrayInputStream(decrypt);
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
        }
        return inputMessage;
    }

    protected String getBody(HttpInputMessage inputMessage) throws IOException {
        Environment environment = applicationContext.getEnvironment();
        boolean enabled = environment.getProperty("server.servlet.encoding.enabled", boolean.class, true);
        Charset charset;
        if (enabled) {
            Encoding encoding = Binder.get(environment).bindOrCreate("server.servlet.encoding", Encoding.class);
            charset = encoding.getCharset();
        } else {
            charset = Optional.of(inputMessage.getHeaders())
                    .map(HttpHeaders::getContentType)
                    .map(MediaType::getCharset)
                    // org.apache.coyote.Constants.DEFAULT_BODY_CHARSET
                    .orElse(StandardCharsets.ISO_8859_1);
        }
        InputStream in = inputMessage.getBody();
        return IoUtil.read(in, charset);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}