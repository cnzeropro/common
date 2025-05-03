package org.zero.common.core.util.spring.context;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.zero.common.core.util.java.reflect.ClassLoaderUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/3
 */
@RequiredArgsConstructor(staticName = "of")
public class MessageSourceHelper {
    @Delegate
    protected final MessageSource messageSource;

    /**
     * 构造 {@link MessageSourceHelper}
     *
     * @param basename like: {@code org.springframework.security.messages}
     * @return {@link MessageSourceHelper}
     */
    public static MessageSourceHelper of(String basename) {
        return of(StandardCharsets.UTF_8.name(), basename);
    }

    /**
     * 构造 {@link MessageSourceHelper}
     *
     * @param basename like: {@code org.springframework.security.messages}
     * @param encoding like: {@code UTF-8}
     * @return {@link MessageSourceHelper}
     */
    public static MessageSourceHelper of(String encoding, String basename) {
        return of(encoding, basename, ClassLoaderUtil.get());
    }

    /**
     * 构造 {@link MessageSourceHelper}
     *
     * @param basename    like: {@code org.springframework.security.messages}
     * @param encoding    like: {@code UTF-8}
     * @param classLoader the {@link ClassLoader} to load resource bundles with
     * @return {@link MessageSourceHelper}
     */
    public static MessageSourceHelper of(String encoding, String basename, ClassLoader classLoader) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(encoding);
        // "ISO-8859-1"
        messageSource.setBundleClassLoader(classLoader);
        return of(messageSource);
    }
}
