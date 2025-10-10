package org.zero.common.core.util.ip2region.xdb;

import lombok.SneakyThrows;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
public class Ip2regionUtil {
	@SneakyThrows
	public static String searchRegion(String dbPath, String ip, Version version) {
		Searcher searcher = Searcher.newWithFileOnly(version, dbPath);
		return searcher.search(ip);
	}

	@SneakyThrows
	public static String searchRegion(String dbPath, byte[] ip, Version version) {
		Searcher searcher = Searcher.newWithFileOnly(version, dbPath);
		return searcher.search(ip);
	}
}
