package org.zero.common.core.extension.logback.core.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LowFilterTest {
	private static ILoggingEvent loggingEvent(Level level) {
		LoggingEvent event = new LoggingEvent();
		event.setLevel(level);
		return event;
	}

	@Test
	void decideShouldAllowLevelsLowerOrEqualToConfiguredLevel() {
		LowFilter filter = new LowFilter();
		filter.setLevel("INFO");
		filter.start();

		assertAll(
				() -> assertEquals(FilterReply.NEUTRAL, filter.decide(loggingEvent(Level.TRACE))),
				() -> assertEquals(FilterReply.NEUTRAL, filter.decide(loggingEvent(Level.DEBUG))),
				() -> assertEquals(FilterReply.NEUTRAL, filter.decide(loggingEvent(Level.INFO))),
				() -> assertEquals(FilterReply.DENY, filter.decide(loggingEvent(Level.WARN))),
				() -> assertEquals(FilterReply.DENY, filter.decide(loggingEvent(Level.ERROR)))
		);
	}

	@Test
	void startShouldKeepFilterNeutralWhenLevelIsInvalid() {
		LowFilter filter = new LowFilter();
		filter.setLevel("invalid");
		filter.start();

		assertAll(
				() -> assertFalse(filter.isStarted()),
				() -> assertEquals(FilterReply.NEUTRAL, filter.decide(loggingEvent(Level.ERROR)))
		);
	}
}
