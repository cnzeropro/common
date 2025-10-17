package org.zero.common.core.util.java.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.util.Random;
import java.util.stream.Collectors;

import static org.zero.common.core.util.java.util.RandomUtil.BASE_LETTER_NUMBER_WITH_UPPER;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
public class RandomHelper {
	@Getter
	@Setter
	@Delegate
	protected Random random;

	public RandomHelper(Random random) {
		this.random = random;
	}

	public int nextInt(int origin, int bound) {
		return nextInt(bound - origin) + origin;
	}

	public short nextShort() {
		return (short) nextInt();
	}

	public short nextShort(short bound) {
		return (short) nextInt(bound);
	}

	public short nextShort(short origin, short bound) {
		return (short) nextInt(origin, bound);
	}

	public char nextChar() {
		return (char) nextInt();
	}

	public char nextChar(char bound) {
		return (char) nextInt(bound);
	}

	public char nextChar(char origin, char bound) {
		return (char) nextInt(origin, bound);
	}

	public char nextChar(CharSequence charSequence) {
		return charSequence.charAt(nextInt(charSequence.length()));
	}

	public String nextString(int size) {
		return nextString(size, BASE_LETTER_NUMBER_WITH_UPPER);
	}

	public String nextString(int size, CharSequence charSequence) {
		return ints(size, 0, charSequence.length())
			.mapToObj(i -> Character.toString(charSequence.charAt(i)))
			.collect(Collectors.joining());
	}

	public char nextChinese() {
		return (char) nextInt('\u4E00', '\u9FFF');
	}

	public String nextChineseString(int size) {
		return ints(size, '\u4E00', '\u9FFF')
			.mapToObj(i -> Character.toString((char) i))
			.collect(Collectors.joining());
	}
}
