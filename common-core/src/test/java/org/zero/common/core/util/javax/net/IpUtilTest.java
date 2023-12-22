package org.zero.common.core.util.javax.net;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/24
 */
class IpUtilTest {

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
        System.out.println(IpUtil.getLocalIpv4());
    }

    @Test
    void getLocalIpv4s() {
        System.out.println(Arrays.toString(IpUtil.getLocalIpv4s()));
    }

    @Test
    void getLocalIpv6s() {
        System.out.println(Arrays.toString(IpUtil.getLocalIpv6s()));
    }

    @Test
    void getLocalIps() {
        System.out.println(Arrays.toString(IpUtil.getLocalIps()));
    }

    @Test
    void getLocalIpsWithInfo() {
        System.out.println(Arrays.toString(IpUtil.getLocalIpsWithInfo()));
    }

    @Test
    void isIpv4() {
        String ip = "1.120.234.0";
        System.out.println(IpUtil.isIpv4(ip));
    }
}