package org.zero.common.core.util.java.net;

import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;

import java.io.File;
import java.net.URI;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/25
 */
public class UriUtil {
    public static URI from(String uri) {
        return CharSequenceUtil.mapIfNonBlank(uri, URI::create);
    }

    public static URI from(File file) {
        return ObjectUtil.mapIfNonNull(file, File::toURI);
    }

    protected UriUtil() {
        throw new UnsupportedOperationException();
    }
}
