package org.zero.common.core.support.api.cipher.strategy;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.CipherMode;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.Getter;
import lombok.Setter;

/**
 * RSA（Rivest-Shamir-Adleman）加解密
 * <p>
 * 算法推荐：RSA/ECB/OAEPWithSHA-256AndMGF1Padding<br>
 * 密钥长度推荐：2048 bit。长度越大，性能越差（加解密速度慢），但安全性越高，反之则反。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Setter
@Getter
public class RSACodec extends BaseCodecStrategy {
    public static final String DEFAULT_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String DEFAULT_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyPASeI0tT7o7eLdfS2l223faCk8oCnAHe7AQqA+38HRxW3wN7wKOmTXzPl/1/7Wa/NL8gixjQU8nQqihVOfB0PalxhkFY39rxh7WK2DM/Yuztokxetax6RNLbF41KI6FjUzbLIA6SJYgmvS9KHcHnAeOQMn4AwJGJYV9+7p5q7BbixOcdY2jEryvd47M+QWX7NFdstwVNqtbsJGXeJ9/8OFdLKvVC0vUhWDwd+SHh+Cnfew/Ji/AgdD7uwd36jJ2gvksL3bpYP6Qm3LeXL3rH2XyevGRYGbGXXra+LXtCSuOXIcTqaAcLgumUaqil60R+ndvhG+wAmpSrOICco/CxQIDAQAB";
    public static final String DEFAULT_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDI8BJ4jS1Pujt4t19LaXbbd9oKTygKcAd7sBCoD7fwdHFbfA3vAo6ZNfM+X/X/tZr80vyCLGNBTydCqKFU58HQ9qXGGQVjf2vGHtYrYMz9i7O2iTF61rHpE0tsXjUojoWNTNssgDpIliCa9L0odwecB45AyfgDAkYlhX37unmrsFuLE5x1jaMSvK93jsz5BZfs0V2y3BU2q1uwkZd4n3/w4V0sq9ULS9SFYPB35IeH4Kd97D8mL8CB0Pu7B3fqMnaC+Swvdulg/pCbct5cvesfZfJ68ZFgZsZdetr4te0JK45chxOpoBwuC6ZRqqKXrRH6d2+Eb7ACalKs4gJyj8LFAgMBAAECggEABtcdWds8eqVVVvOvG8sYT7pRwt4U993+czh4KLB4S4g7P6kGdGd64T5T7ICpyC2vfAHWtLu8GUIPjRZef5xwHaP32rmJVGzHB3SPR+TAtbTmxkT/WTYr0kpk2+iNGLdZVokSQP6QfV4W+A9yzQjYSnh7YDs/5SJTrv1PX51mfPCxi88LhKHfK/LtNVoIcgIHHGXBkvxuqwpBXTQ7oAoUWQP2wEzE9aHC9XZgUFR/eO67ExpZC905X7IkotaOFOY5C13ibwcQ6h67k0eSE72xKBH28ja8H+nDUx8GofU1VaizbIWJSdFDCfAgJfwqH+G6IU2bT+5Anv1MKWCWJBWLdQKBgQDk2alE7uVVxpMwXDH7youjHVjdA8ETroYGul4WTYGGEYVgFoW/01lM5b6nGfOLE8jGdl9IBbU6f7AnshAeIcJ75vXeKyR+iL02rVPHrfR5FLySSKLHU9XaWq91TzkcKnIfEhAEKSYf47C1yyYU3ACFecsZoHdvetBVSIM/6Cof1wKBgQDgxrDCRwsBszGCvzF3VISdNbmunL7KDhBmzzvJMrlp2rvUNNdWO+F6vwk9d40t3CUwVq1r6iwfxzQur+abXbAg+2AoY9lAXfnIxiw+WXEaLES9sdP4rTl1JYXGKnstBuGziidyt5tHT9geS/YGjl23ahko2R5mQO1HU4gCYKpOwwKBgQDAEd57OHXKU+tc/0QEK66erBbVCD2YIzXhl7E3zr0SpMnoJ38BzIR6gahN3v4EkApGZzr427GK83gJGO1mHLd5/hIE8PikKmeD6F0Uje9NYBYUQFo+KSKnLM1uC5vHA+jcIvDGTTLuOM8rBOFlSHA3lkzbqU4UhmMr8LwXeuc/6QKBgQCFdlRDtq0ZYE24hU+YvA1Vzy2mORmrnXgto0SrbTvcV20JTirB4CI8oKua3J5uKXXHYt/r4Io8gCpCwlzZyWIn4zowIUFAz8vdY5Wnm7HlSX2zaKAk2q3wGcx5YoXqsVhYI2LS4aQITqdTLeUx9Vw96Hhx4aEEM+7vV/C9AKp+wwKBgGHhlWs+FWlK07x+0XCh0CgSI0JqtJbpWbLN7GHgLX5Sh0wlON/66nidnhF/U5ROhwjMltD/DIDkAEDGDTMN0JbadHmIPP0g4Pdz4IIZhHSf37IEGPVq7K5dee6AW9Bz7EeO1x1TnZVTnYD13WFm9w1sVVZWRrtYXlZ+aWNOfUdp";

    private final RSA rsa;

    @Override
    public byte[] encrypt(byte[] source) {
        return rsa.encrypt(source, KeyType.PublicKey);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return rsa.decrypt(source, KeyType.PrivateKey);
    }

    public static RSACodec of(StrategyContext context) {
        return new RSACodec(context);
    }

    protected RSACodec(StrategyContext context) {
        super(context);
        String algorithm = context.getAlgorithm();
        CipherMode cipherMode = context.getCipherMode();
        byte[] key = context.getKey();
        String privateKeyStr = null;
        String publicKeyStr = null;
        if (CipherMode.encrypt.equals(cipherMode)) {
            publicKeyStr = Base64.encode(key);
        }
        if (CipherMode.decrypt.equals(cipherMode)) {
            privateKeyStr = Base64.encode(key);
        }
        this.rsa = new RSA(algorithm, publicKeyStr, privateKeyStr);
    }
}
