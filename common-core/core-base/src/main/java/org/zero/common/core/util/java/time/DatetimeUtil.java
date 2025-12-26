package org.zero.common.core.util.java.time;

import lombok.experimental.UtilityClass;

import java.util.Calendar;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@UtilityClass
public class DatetimeUtil {
	/**
	 * Days per week.
	 */
	public static final int DAYS_PER_WEEK = 7;

	/**
	 * Hours per day.
	 */
	public static final int HOURS_PER_DAY = 24;
	/**
	 * Hours per week.
	 */
	public static final int HOURS_PER_WEEK = HOURS_PER_DAY * DAYS_PER_WEEK;

	/**
	 * Minutes per hour.
	 */
	public static final int MINUTES_PER_HOUR = 60;
	/**
	 * Minutes per day.
	 */
	public static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
	/**
	 * Minutes per week.
	 */
	public static final int MINUTES_PER_WEEK = MINUTES_PER_DAY * DAYS_PER_WEEK;

	/**
	 * Seconds per minute.
	 */
	public static final int SECONDS_PER_MINUTE = 60;
	/**
	 * Seconds per hour.
	 */
	public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
	/**
	 * Seconds per day.
	 */
	public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
	/**
	 * Seconds per week.
	 */
	public static final int SECONDS_PER_WEEK = SECONDS_PER_DAY * DAYS_PER_WEEK;

	/**
	 * Milliseconds per second.
	 */
	public static final int MILLIS_PER_SECOND = 1000;
	/**
	 * Milliseconds per minute.
	 */
	public static final int MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE;
	/**
	 * Milliseconds per hour.
	 */
	public static final int MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR;
	/**
	 * Milliseconds per day.
	 */
	public static final int MILLIS_PER_DAY = MILLIS_PER_HOUR * HOURS_PER_DAY;
	/**
	 * Milliseconds per week.
	 */
	public static final int MILLIS_PER_WEEK = MILLIS_PER_DAY * DAYS_PER_WEEK;

	/**
	 * Microseconds per millisecond.
	 */
	public static final int MICROS_PER_MILLI = 1000;
	/**
	 * Microseconds per second.
	 */
	public static final int MICROS_PER_SECOND = MICROS_PER_MILLI * MILLIS_PER_SECOND;
	/**
	 * Microseconds per minute.
	 */
	public static final int MICROS_PER_MINUTE = MICROS_PER_SECOND * SECONDS_PER_MINUTE;
	/**
	 * Microseconds per hour.
	 */
	public static final long MICROS_PER_HOUR = (long) MICROS_PER_MINUTE * MINUTES_PER_HOUR;
	/**
	 * Microseconds per day.
	 */
	public static final long MICROS_PER_DAY = MICROS_PER_HOUR * HOURS_PER_DAY;
	/**
	 * Microseconds per week.
	 */
	public static final long MICROS_PER_WEEK = MICROS_PER_DAY * DAYS_PER_WEEK;

	/**
	 * Nanoseconds per microsecond.
	 */
	public static final int NANOS_PER_MICRO = 1000;
	/**
	 * Nanoseconds per millisecond.
	 */
	public static final int NANOS_PER_MILLI = NANOS_PER_MICRO * MICROS_PER_MILLI;
	/**
	 * Nanoseconds per second.
	 */
	public static final int NANOS_PER_SECOND = NANOS_PER_MILLI * MILLIS_PER_SECOND;
	/**
	 * Nanoseconds per minute.
	 */
	public static final long NANOS_PER_MINUTE = (long) NANOS_PER_SECOND * SECONDS_PER_MINUTE;
	/**
	 * Nanoseconds per hour.
	 */
	public static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;
	/**
	 * Nanoseconds per day.
	 */
	public static final long NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY;
	/**
	 * Nanoseconds per week.
	 */
	public static final long NANOS_PER_WEEK = NANOS_PER_DAY * DAYS_PER_WEEK;

	/**
	 * 根据蔡勒公式计算任意一个日期是星期几
	 * <p>
	 * 注意：仅支持1582年10月15日之后的格里高利历（公历），不支持公元前年份
	 *
	 * @param calendar {@link Calendar}
	 * @return 中国星期（从星期一开始，星期日是7）
	 */
	public static int calculateWeek(Calendar calendar) {
		return calculateWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * 根据蔡勒公式计算任意一个日期是星期几
	 * <p>
	 * 注意：仅支持1582年10月15日之后的格里高利历（公历），不支持公元前年份
	 *
	 * @param year  年
	 * @param month 月
	 * @param day   日
	 * @return 中国星期（从星期一开始，星期日是7）
	 */
	public static int calculateWeek(int year, int month, int day) {
		if (month == 1) {
			month = 13;
			year--;
		}
		if (month == 2) {
			month = 14;
			year--;
		}
		int y = year % 100;
		int c = year / 100;
		int h = (y + (y / 4) + (c / 4) - (2 * c) + ((26 * (month + 1)) / 10) + day - 1) % 7;
		// 可能是负值，因此计算除以7的余数之后需要判断是大于等于0还是小于0，如果小于0则将余数加7
		if (h < 0) {
			h += 7;
		}

		// 国内理解中星期日为 7
		if (h == 0) {
			return 7;
		}
		return h;
	}
}
