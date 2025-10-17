package org.zero.common.core.util.ip2region.xdb.v2;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.lionsoul.ip2region.xdb.Searcher;
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
	@Delegate
	protected final Searcher searcher;

	public static Ip2regionHelper of() {
		return of(DEFAULT_REGION_FILE);
	}

	@SneakyThrows
	public static Ip2regionHelper of(String path) {
		URL url = UrlUtil.fromPath(path);
		@Cleanup InputStream inputStream = UrlUtil.openStream(url);
		byte[] bytes = IoUtil.readAll(inputStream);
		return of(bytes);
	}

	@SneakyThrows
	public static Ip2regionHelper of(byte[] cBuff) {
		return of(Searcher.newWithBuffer(cBuff));
	}
}
