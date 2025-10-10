package org.zero.common.core.util.ip2region.xdb;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.net.UrlUtil;

import java.io.InputStream;
import java.net.URL;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/11
 */
@RequiredArgsConstructor(staticName = "of")
public class Ip2regionHelper {
	public static final String DEFAULT_REGION_FILE = "classpath:/ip2region.xdb";
	public static final Version DEFAULT_VERSION = Version.IPv4;
	@Delegate
	protected final Searcher searcher;

	public static Ip2regionHelper of() {
		return of(DEFAULT_VERSION);
	}

	public static Ip2regionHelper of(Version version) {
		return of(DEFAULT_REGION_FILE, version);
	}

	@SneakyThrows
	public static Ip2regionHelper of(String path, Version version) {
		URL url = UrlUtil.fromPath(path);
		@Cleanup InputStream inputStream = UrlUtil.openStream(url);
		LongByteArray longByteArray = new LongByteArray();
		byte[] bytes = new byte[IoUtil.DEFAULT_BUFFER_SIZE];
		while (inputStream.read(bytes) != -1) {
			longByteArray.append(bytes);
		}
		return of(longByteArray, version);
	}

	@SneakyThrows
	public static Ip2regionHelper of(LongByteArray longByteArray, Version version) {
		return of(Searcher.newWithBuffer(version, longByteArray));
	}
}
