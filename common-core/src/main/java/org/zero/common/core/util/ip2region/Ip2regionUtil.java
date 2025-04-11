package org.zero.common.core.util.ip2region;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import org.lionsoul.ip2region.xdb.Searcher;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/11
 */
public class Ip2regionUtil {
    public static final String DEFAULT_REGION_FILE = "ip2region.xdb";
    private static volatile Searcher SEARCHER;

    @SneakyThrows
    public static Searcher getSearcher() {
        if (SEARCHER == null) {
            synchronized (Ip2regionUtil.class) {
                if (SEARCHER == null) {
                    byte[] bytes = ResourceUtil.readBytes(DEFAULT_REGION_FILE);
                    SEARCHER = Searcher.newWithBuffer(bytes);
                }
            }
        }
        return SEARCHER;
    }

    @SneakyThrows
    public static String search(String ip) {
        return getSearcher().search(ip);
    }

    protected Ip2regionUtil() {
        throw new UnsupportedOperationException();
    }
}
