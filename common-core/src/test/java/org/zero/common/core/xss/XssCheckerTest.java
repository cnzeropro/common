package org.zero.common.core.xss;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/23
 */
class XssCheckerTest {

    @Test
    void check() {
        String str = "modeling & US MSRP $81.99, (Not Included)";
        XssChecker.checkOrElseThrow(str);
        boolean checked = XssChecker.check(str);
        System.out.println(checked);
    }
}