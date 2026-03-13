package org.zero.common.core.util.javax.net;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.net.InetAddressUtil;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/24
 */
class InetAddressUtilTest {

    @Test
    void getRemoteIp() {
    }

    @Test
    void getRemoteIps() {
    }

    @Test
    void getRemoteIpsStr() {
    }

    @Test
    void getLocalIpv4() {
        System.out.println(InetAddressUtil.getLocalIpv4());
    }

    @Test
    void getLocalIpv4s() {
        System.out.println(Arrays.toString(InetAddressUtil.getLocalIpv4s()));
    }

    @Test
    void getLocalIpv6s() {
        System.out.println(Arrays.toString(InetAddressUtil.getLocalIpv6s()));
    }

    @Test
    void getLocalIps() {
        System.out.println(Arrays.toString(InetAddressUtil.getLocalIps()));
    }

    @Test
    void getLocalIpsWithInfo() {
        System.out.println(Arrays.toString(InetAddressUtil.getLocalIpsWithInfo()));
    }

    @Test
    void isIpv4() {
        // 有效地址
        assertTrue(InetAddressUtil.isIPv4("0.0.0.0"));          // 全零地址
        assertTrue(InetAddressUtil.isIPv4("192.168.1.1"));      // 常规地址
        assertTrue(InetAddressUtil.isIPv4("255.255.255.255"));  // 最大地址

        // 无效地址
        assertFalse(InetAddressUtil.isIPv4("192.168.01.1"));    // 含前导零
        assertFalse(InetAddressUtil.isIPv4("256.0.0.0"));       // 超范围数值
        assertFalse(InetAddressUtil.isIPv4("1.2.3.4.5"));       // 多段异常
        assertFalse(InetAddressUtil.isIPv4("2001:db8::1"));     // IPv6地址干扰
    }
}