package org.zero.common.core.util.java.util;

import lombok.SneakyThrows;
import org.zero.common.data.exception.UtilException;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/28
 */
public class RandomUtil {
	/**
	 * 用于随机选的数字
	 */
	public static final String BASE_NUMBER = "0123456789";
	/**
	 * 用于随机选的字母
	 */
	public static final String BASE_LETTER = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * 用于随机选的字符
	 */
	public static final String BASE_CHAR = "~!@#$%^&*_-+=|";

	/**
	 * 用于随机选的字母和数字，默认只有字母小写
	 */
	public static final String BASE_LETTER_NUMBER = BASE_LETTER + BASE_NUMBER;

	/**
	 * 用于随机选的字母和数字，包含字母大写
	 */
	public static final String BASE_LETTER_NUMBER_WITH_UPPER = BASE_LETTER.toUpperCase() + BASE_LETTER_NUMBER;

	/**
	 * 用于随机选的字符、字母和数字，默认只有字母小写
	 */
	public static final String BASE_CHAR_LETTER_NUMBER = BASE_LETTER_NUMBER + BASE_CHAR;

	/**
	 * 用于随机选的字符、字母和数字，包含字母大写
	 */
	public static final String BASE_CHAR_LETTER_NUMBER_WITH_UPPER = BASE_LETTER_NUMBER_WITH_UPPER + BASE_CHAR;

	public static Random getRandom(RandomType randomType) {
		switch (randomType) {
			case RANDOM:
				return getRandom();
			case THREAD_LOCAL_RANDOM:
				return getThreadLocalRandom();
			case SECURE_RANDOM:
				return getSecureRandom();
			default:
				throw new UtilException("Unknown random type");
		}
	}

	public static Random getRandom() {
		return new Random();
	}

	public static ThreadLocalRandom getThreadLocalRandom() {
		return ThreadLocalRandom.current();
	}

	public static SecureRandom getSecureRandom() {
		return new SecureRandom();
	}

	@SneakyThrows
	public static SecureRandom getStrongRandom() {
		return SecureRandom.getInstanceStrong();
	}

	@SneakyThrows
	public static SecureRandom getSecureRandom(String algorithm) {
		return SecureRandom.getInstance(algorithm);
	}

	@SneakyThrows
	public static SecureRandom getSecureRandom(RandomAlgorithm algorithm) {
		return SecureRandom.getInstance(algorithm.getAlgorithm());
	}

	protected static final RandomHelper randomHelper = new RandomHelper(getRandom());

	public static int randomInt(int bound) {
		return randomHelper.nextInt(bound);
	}

	public static int randomInt(int minInclusive, int maxExclusive) {
		return randomHelper.nextInt(minInclusive, maxExclusive);
	}

	public static char randomChar() {
		return randomHelper.nextChar();
	}

	public static char randomChar(CharSequence charSequence) {
		return randomHelper.nextChar(charSequence);
	}

	public static String randomString(int size) {
		return randomHelper.nextString(size);
	}

	public static String randomString(int size, CharSequence charSequence) {
		return randomHelper.nextString(size, charSequence);
	}

	public static char randomChinese() {
		return randomHelper.nextChinese();
	}

	public static String randomChineseString(int size) {
		return randomHelper.nextChineseString(size);
	}
}
