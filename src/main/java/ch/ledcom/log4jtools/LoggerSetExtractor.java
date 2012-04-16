package ch.ledcom.log4jtools;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.spi.LoggingEvent;

import ch.ledcom.log4jtools.processor.LogProcessor;

import com.google.common.collect.ImmutableSet;

public class LoggerSetExtractor implements LogProcessor {

	private final Set<String> loggers = new HashSet<String>();
	
	@Override
	public void process(LoggingEvent event) throws IOException {
		this.loggers.add(event.getLoggerName());
	}
	
	public Set<String> getLoggers() {
		return ImmutableSet.copyOf(this.loggers);
	}
}
