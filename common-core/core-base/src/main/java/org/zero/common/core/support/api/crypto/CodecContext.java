package org.zero.common.core.support.api.crypto;

import cn.hutool.crypto.CipherMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.zero.common.core.support.api.crypto.supplier.KeySupplier;

import java.util.Properties;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CodecContext {
    private String algorithm;
    private byte[] key;
    private KeySupplier keySupplier;
    private StringMode stringMode;
    private CipherMode cipherMode;
    private Properties properties;
}
