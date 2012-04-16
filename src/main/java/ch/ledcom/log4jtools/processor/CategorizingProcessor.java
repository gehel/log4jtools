package ch.ledcom.log4jtools.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;

import ch.ledcom.log4jtools.Report;
import ch.ledcom.log4jtools.filter.CategorizationFilter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class CategorizingProcessor implements LogProcessor {

	private final List<CategorizationFilter> filters;

	private final Map<String, Appender> appenders;

	private final Map<String, Report> reports = new HashMap<String, Report>();

	public CategorizingProcessor(List<CategorizationFilter> filters,
			final Map<String, Appender> appenders) throws IOException {
		this.filters = filters;
		this.appenders = appenders;
	}

	@Override
	public void process(LoggingEvent event) throws IOException {
		for (CategorizationFilter filter : this.filters) {
			if (filter.match(event)) {
				processEvent(filter, event, this.reports);
				break;
			}
		}
	}

	private void processEvent(CategorizationFilter filter, LoggingEvent event,
			Map<String, Report> reports) throws IOException {
		// log to the appropriate file
		Appender appender = getAppender(filter);
		if (appender != null) {
			appender.doAppend(event);
		}
		getReport(filter, reports).accumulateEvent(event);
	}

	private Appender getAppender(CategorizationFilter filter) {
		if (Strings.isNullOrEmpty(filter.getCategory())) {
			return NullAppender.getNullAppender();
		}
		return this.appenders.get(filter.getCategory());
	}

	private Report getReport(CategorizationFilter filter,
			Map<String, Report> reports) {
		String description = filter.getDescription();
		if (!reports.containsKey(description)) {
			reports.put(description, new Report(description));
		}
		return reports.get(description);
	}

	public Map<String, Report> getReports() {
		return ImmutableMap.copyOf(this.reports);
	}

}
