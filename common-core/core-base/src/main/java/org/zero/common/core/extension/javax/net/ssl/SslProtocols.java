package org.zero.common.core.extension.javax.net.ssl;

/**
 * SSL/TLS 协议标准名称集合。
 *
 * <p>这些常量可用于 {@link javax.net.ssl.SSLContext#getInstance(String)}、
 * {@link javax.net.ssl.SSLSocket#setEnabledProtocols(String[])} 等 JDK SSL API。</p>
 *
 * <p>该接口只保存协议名称，不表示这些协议都安全或推荐启用。SSLv2、SSLv3、TLSv1.0、TLSv1.1
 * 均属于历史兼容协议，现代 JDK 和安全策略通常会默认禁用；新连接应优先使用运行时支持的强 TLS 版本。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public interface SslProtocols {
	/* ********************************************************* SSL (Secure Sockets Layer) ********************************************************* */
    /**
	 * SSL 协议族名称。
	 *
	 * <p>由具体 Provider 决定可用版本；仅用于兼容旧 API 或旧环境，新代码通常应优先使用 {@link #TLS}。</p>
     */
    String SSL = "SSL";
    /**
	 * SSL 2.0 协议名称。
	 *
	 * <p>该协议已不安全，现代运行时通常不再支持或默认禁用，仅保留常量用于识别旧配置。</p>
     */
    String SSLv2 = "SSLv2";
    /**
	 * SSL 3.0 协议名称。
	 *
	 * <p>该协议已不安全，现代运行时通常不再支持或默认禁用，仅保留常量用于旧系统兼容。</p>
     */
    String SSLv3 = "SSLv3";

    /* ********************************************************* TLS (Transport Layer Security) ********************************************************* */
    /**
	 * TLS 协议族名称。
	 *
	 * <p>通常作为 {@link javax.net.ssl.SSLContext} 的默认协议入口，由 Provider 选择可用 TLS 版本。</p>
     */
    String TLS = "TLS";
    /**
	 * TLS 1.0 协议名称，定义于 RFC 2246。
	 *
	 * <p>该协议已属于历史兼容协议，现代运行时通常默认禁用，不建议用于新连接。</p>
     */
    String TLSv1 = "TLSv1";
    /**
	 * TLS 1.1 协议名称，定义于 RFC 4346。
	 *
	 * <p>该协议已属于历史兼容协议，现代运行时通常默认禁用，不建议用于新连接。</p>
     */
    String TLSv11 = "TLSv1.1";
    /**
	 * TLS 1.2 协议名称，定义于 RFC 5246。
	 *
	 * <p>Java 8 及大量存量系统中常见的 TLS 安全基线；新环境可优先使用运行时支持的更高 TLS 版本。</p>
     */
    String TLSv12 = "TLSv1.2";
	/**
	 * TLS 1.3 协议名称，定义于 RFC 8446。
	 *
	 * <p>现代 TLS 版本；是否可用取决于当前 JDK、Provider 和运行时安全策略。</p>
	 */
	String TLSv13 = "TLSv1.3";

	/* ********************************************************* DTLS (Datagram Transport Layer Security) ********************************************************* */
	/**
	 * DTLS 协议族名称。
	 *
	 * <p>用于基于 UDP 的 TLS 场景；是否可用取决于当前 JDK 与 Provider。</p>
	 */
	String DTLS = "DTLS";
	/**
	 * DTLS 1.0 协议名称。
	 *
	 * <p>历史兼容协议，现代场景通常优先选择更高版本。</p>
	 */
	String DTLSv10 = "DTLSv1.0";
	/**
	 * DTLS 1.2 协议名称。
	 *
	 * <p>常见 DTLS 安全基线；实际可用性取决于当前 JDK 与 Provider。</p>
	 */
	String DTLSv12 = "DTLSv1.2";
}
