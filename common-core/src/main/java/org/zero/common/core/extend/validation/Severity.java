package org.zero.common.core.extend.validation;

import javax.validation.Payload;

/**
 * @author zero
 * @since 2021/4/15
 */
public class Severity {
    public static class Debug implements Payload {
    }

    public static class Info implements Payload {
    }

    public static class Error implements Payload {
    }
}
