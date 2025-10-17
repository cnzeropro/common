package org.zero.common.core.support.api.cipher.strategy;

import cn.hutool.crypto.CipherMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Properties;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/27
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StrategyContext {
    private String algorithm;
    private byte[] key;
    private CipherMode cipherMode;
    private Properties properties;
}
