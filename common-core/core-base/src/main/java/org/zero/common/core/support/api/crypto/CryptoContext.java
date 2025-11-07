package org.zero.common.core.support.api.crypto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.zero.common.core.extension.java.util.MapHelper;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CryptoContext {
	private String algorithm;
	private byte[] key;
	private CryptoMode cryptoMode;
	private MapHelper config;
	@EqualsAndHashCode.Exclude
	private StringMode sourceStringMode;
	@EqualsAndHashCode.Exclude
	private StringMode targetStringMode;
}
