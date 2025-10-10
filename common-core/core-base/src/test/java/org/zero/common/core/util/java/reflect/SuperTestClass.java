package org.zero.common.core.util.java.reflect;

import java.math.BigDecimal;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/14
 */
public class SuperTestClass {
    public String publicSuperField;
    protected String protectedSuperField;
    String defaultSuperField;
    private String privateSuperField;

    public void publicSuperMethod(CharSequence a, BigDecimal b) {
    }

    public void publicSuperMethod(String a, Number b) {
    }

    protected void protectedSuperMethod() {
    }

    void defaultSuperMethod() {
    }

    static String defaultStaticSuperMethod() {
        return "defaultStaticSuperMethod";
    }

    private void privateSuperMethod() {
    }
}
