package org.zero.common.core.util.java.reflect;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/14
 */
public class SubTestClass extends SuperTestClass{
    public String publicSubField;
    protected String protectedSubField;
    String defaultSubField;
    private String privateSubField;

    public void publicSubMethod() {
    }

    protected void protectedSubMethod() {
    }

    void defaultSubMethod() {
    }

    private void privateSubMethod() {
    }
}
