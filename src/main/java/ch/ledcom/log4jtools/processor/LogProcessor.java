package ch.ledcom.log4jtools.processor;

import java.io.IOException;

import org.apache.log4j.spi.LoggingEvent;

public interface LogProcessor {
	void process(LoggingEvent event) throws IOException;
}
