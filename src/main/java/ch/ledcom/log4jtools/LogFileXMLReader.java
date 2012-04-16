package ch.ledcom.log4jtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.Decoder;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLDecoder;

import ch.ledcom.log4jtools.processor.LogProcessor;

import com.google.common.collect.ImmutableList;

public class LogFileXMLReader {

	private final Decoder decoder;

	private final List<LogProcessor> logProcessors;

	public LogFileXMLReader(List<LogProcessor> logProcessors) throws IOException {
		this.decoder = new XMLDecoder();
		this.logProcessors = ImmutableList.copyOf(logProcessors);
	}

	@SuppressWarnings("unchecked")
	public void process(BufferedReader reader, String host, String path)
			throws IOException {
		char[] content = new char[10000];
		int length = 0;
		while ((length = reader.read(content)) > -1) {
			processEvents(
					decoder.decodeEvents(String.valueOf(content, 0, length)),
					host, path);
		}
	}

	private void processEvents(Collection<LoggingEvent> c, String host,
			String application) throws IOException {
		if (c == null) {
			return;
		}
		for (LoggingEvent evt : c) {
			if (evt.getProperty(Constants.HOSTNAME_KEY) == null) {
				evt.setProperty(Constants.HOSTNAME_KEY, host);
			}
			if (evt.getProperty(Constants.APPLICATION_KEY) == null) {
				evt.setProperty(Constants.APPLICATION_KEY, application);
			}

			for (LogProcessor processor : this.logProcessors) {
				processor.process(evt);
			}
		}
	}

}
