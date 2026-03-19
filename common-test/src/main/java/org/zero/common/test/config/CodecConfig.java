package org.zero.common.test.config;

import org.springframework.context.annotation.Import;
import org.zero.common.core.support.api.crypto.encryption.EncryptionResponseBodyAdvice;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/26
 */
@Import(EncryptionResponseBodyAdvice.class)
// @Configuration(proxyBeanMethods = false)
public class CodecConfig {
}
