package org.zero.common.core.extension.javax.net.ssl;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public interface SslProtocols {
    /* ********************************************************* SSL (Secure Socket Layer) ********************************************************* */
    /**
     * Supports some version of SSL; may support other versions
     */
    String SSL = "SSL";
    /**
     * Supports SSL version 2 or later; may support other versions
     */
    String SSLv2 = "SSLv2";
    /**
     * Supports SSL version 3; may support other versions
     */
    String SSLv3 = "SSLv3";

    /* ********************************************************* TLS (Transport Layer Security) ********************************************************* */
    /**
     * Supports some version of TLS; may support other versions
     */
    String TLS = "TLS";
    /**
     * Supports RFC 2246: TLS version 1.0 ; may support other versions
     */
    String TLSv1 = "TLSv1";
    /**
     * Supports RFC 4346: TLS version 1.1 ; may support other versions
     */
    String TLSv11 = "TLSv1.1";
    /**
     * Supports RFC 5246: TLS version 1.2 ; may support other versions
     */
    String TLSv12 = "TLSv1.2";
}
