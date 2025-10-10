package org.zero.common.core.extension.javax.net.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public class TrustAnyHostnameVerifier implements HostnameVerifier {
    public static final TrustAnyHostnameVerifier INSTANCE = new TrustAnyHostnameVerifier();

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
