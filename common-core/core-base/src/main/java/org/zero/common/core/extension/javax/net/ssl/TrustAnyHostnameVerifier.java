package org.zero.common.core.extension.javax.net.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 信任任意主机名的 {@link HostnameVerifier} 实现。
 *
 * <p>该类会跳过 HTTPS 主机名校验，即不校验证书中的 CN / SAN 是否与目标主机匹配。
 * 它只影响主机名校验，不负责证书链校验；证书链是否可信由 TrustManager 决定。</p>
 *
 * <p>该类只应在受控测试、内网联调或明确兼容场景中显式使用。
 * 生产环境通常应使用 JDK 或 HTTP 客户端默认的 HostnameVerifier。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public class TrustAnyHostnameVerifier implements HostnameVerifier {
	/**
	 * 信任任意主机名的共享实例。
	 */
    public static final HostnameVerifier INSTANCE = new TrustAnyHostnameVerifier();

    @Override
    public boolean verify(String hostname, SSLSession session) {
		// 始终放行主机名校验。
        return true;
    }
}
