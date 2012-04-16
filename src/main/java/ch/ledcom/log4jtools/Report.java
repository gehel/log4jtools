package ch.ledcom.log4jtools;

import java.io.IOException;

import org.apache.log4j.spi.LoggingEvent;

public class Report {

	private final String description;

	private int occurences = 0;

	public Report(String description) {
		this.description = description;
	}

	public void accumulateEvent(LoggingEvent event) throws IOException {
		this.occurences++;
	}

	public String getDescription() {
		return this.description;
	}

	public int getOccurences() {
		return this.occurences;
	}

}
