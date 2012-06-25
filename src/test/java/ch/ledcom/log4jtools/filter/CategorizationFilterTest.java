package ch.ledcom.log4jtools.filter;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

public class CategorizationFilterTest {

	@Test
	public void matchLoggerName() {
		CategorizationFilter filter = new CategorizationFilter("",
				"loggerName", (Level) null, (Pattern) null, (Pattern) null,
				"category", "bugTrackerRef");
		LoggingEvent event = new LoggingEvent("",
				Logger.getLogger("loggerName"), 0, (Level) null, null,
				(Throwable) null);
		assertTrue(filter.match(event));
	}

	@Test
	public void matchLevel() {
		CategorizationFilter filter = new CategorizationFilter("",
				(String) null, Level.INFO, (Pattern) null, (Pattern) null,
				"category", "bugTrackerRef");
		LoggingEvent event = new LoggingEvent("",
				Logger.getLogger("loggerName"), 0, Level.INFO, null,
				(Throwable) null);
		assertTrue(filter.match(event));
	}

	@Test
	public void matchMessage() {
		CategorizationFilter filter = new CategorizationFilter("",
				(String) null, Level.INFO, Pattern.compile(
						"First line of each file following a roll.*",
						Pattern.DOTALL), (Pattern) null, "category",
				"bugTrackerRef");
		LoggingEvent event = new LoggingEvent("",
				Logger.getLogger("loggerName"), 0, Level.INFO,
				"First line of each file following a roll\n", (Throwable) null);
		assertTrue(filter.match(event));
	}
}
