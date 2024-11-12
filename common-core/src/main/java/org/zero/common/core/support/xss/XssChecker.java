package org.zero.common.core.support.xss;

import org.zero.common.core.util.java.lang.StringUtil;

import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2021/8/23
 */
public final class XssChecker {
    private static final Pattern XSS_PATTERN = Pattern.compile("(<[^>]+>|</[^>]+>|on\\w+|javascript:|alert\\(|prompt\\(|document\\.write\\(|eval\\(|iframe)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private XssChecker() {
    }

    public static boolean check(final String input) {
        if (StringUtil.isBlank(input)) {
            return false;
        }
        return XSS_PATTERN.matcher(input).find();
    }

    public static void checkOrElseThrow(final String input) {
        checkOrElseThrow(input, XssException::new);
    }

    public static <X extends Throwable> void checkOrElseThrow(final String input, Supplier<? extends X> exceptionSupplier) throws X {
        if (check(input)) {
            throw exceptionSupplier.get();
        }
    }
}
