package org.zero.common.core.extension.java.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

/**
 * 安全相关 Builder 测试辅助类。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
public final class SecurityBuilderTestSupport {
	private static final String CERTIFICATE_PEM =
			"-----BEGIN CERTIFICATE-----\n"
					+ "MIIDhzCCAm+gAwIBAgIEZbbDEjANBgkqhkiG9w0BAQsFADB0MQswCQYDVQQGEwJD\n"
					+ "TjERMA8GA1UECBMIU2hhbmdoYWkxETAPBgNVBAcTCFNoYW5naGFpMQ0wCwYDVQQK\n"
					+ "EwRaZXJvMQ0wCwYDVQQLEwRUZXN0MSEwHwYDVQQDExhTZWN1cml0eSBCdWlsZGVy\n"
					+ "IEZpeHR1cmUwHhcNMjYwNDE3MDk1MDQ1WhcNMzYwNDE0MDk1MDQ1WjB0MQswCQYD\n"
					+ "VQQGEwJDTjERMA8GA1UECBMIU2hhbmdoYWkxETAPBgNVBAcTCFNoYW5naGFpMQ0w\n"
					+ "CwYDVQQKEwRaZXJvMQ0wCwYDVQQLEwRUZXN0MSEwHwYDVQQDExhTZWN1cml0eSBC\n"
					+ "dWlsZGVyIEZpeHR1cmUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCf\n"
					+ "hzppxJ5XG355zlKr9DCeSDZwePZPTq3/FI3hJUKsBee1+Rg3TmkMNomw3sc9z/kf\n"
					+ "bFM/b9FcKpJ1n2uosk6C2UvavNOdrKpAwQ9S2uIRGfej5jx7EQ7mDqK34MthPIQY\n"
					+ "Pr+BoU++dHCh0gKZR0mAlJk8yFi1hQUGAb/t/ojAFIXwbfuxGOp9Sej0gwBNpuzd\n"
					+ "C6I8ws1NwaHTZ0hkiDHiXo1AELeJORYs05cRyZSB8QOApWi3/b9ZJ541SUdO43eM\n"
					+ "G0+o/OCHUREQ6ucRX7uAsZ8BP8D0n52cQljv9lY2xPNWpbzRRhihVkC/ybAgwj0R\n"
					+ "39d5xCBT5FlLxQQb1zTrAgMBAAGjITAfMB0GA1UdDgQWBBTcMNiXwyO5zqzACpBP\n"
					+ "0ap9aAm7VjANBgkqhkiG9w0BAQsFAAOCAQEAFGntnCbqDG3OwJslVM8gBZhRN6iJ\n"
					+ "KlDclYwf0A1Va9CQu1soE3Ryoqa1oz8rdByHCQiSWsXORvhsLJFw8S7+Quh+IByo\n"
					+ "sHUuPf/M0cFcAu/OAK+3aWDxZXlcmqwIE4S61YeQYpU0Wtuf2PJXEp9lwJeqIq5G\n"
					+ "ABSl+M9JxIbv5Kh8bQauhMzJxAeNrZa69dfb7o+xU0SyaQRaGKSbc5efRdWjY4y3\n"
					+ "RuJPgfUL6eZrtghO0LfSPStABrPefUW4q+tu0b7+FB8i7CLobYyjMlVDWR8dHPxB\n"
					+ "8ZngBrDzKF1ADm4NPiYS0Q/H6uz/S3hWmiBOJEbbR3hYJimWVCba8GMq0Q==\n"
					+ "-----END CERTIFICATE-----\n";

	private SecurityBuilderTestSupport() {
	}

	public static byte[] createEmptyKeyStoreBytes(String type, char[] password) throws Exception {
		KeyStore keyStore = KeyStore.getInstance(type);
		keyStore.load(null, password);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		keyStore.store(outputStream, password);
		return outputStream.toByteArray();
	}

	public static byte[] certificateBytes() {
		return CERTIFICATE_PEM.getBytes(StandardCharsets.US_ASCII);
	}

	public static Path writeTestFile(String prefix, String suffix, byte[] content) throws IOException {
		Path directory = Paths.get("build", "generated-test-files", "security-builder");
		Files.createDirectories(directory);
		Path path = Files.createTempFile(directory, prefix, suffix);
		Files.write(path, content);
		return path;
	}
}
