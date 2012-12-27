/**
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package ch.ledcom.log4jtools.filter;

import java.util.regex.Pattern;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class CategorizationFilter {

	private final String description;
	private final String loggerName;
	private final Level level;
	private final Pattern messagePattern;
	private final Pattern throwablePattern;
	private final String category;
	private final String bugTrackerRef;

	public CategorizationFilter(String description, String loggerName,
			Level level, Pattern messagePattern, Pattern throwablePattern,
			String category, String bugTrackerRef) {
		this.description = description;
		this.loggerName = loggerName;
		this.level = level;
		this.messagePattern = messagePattern;
		this.throwablePattern = throwablePattern;
		this.category = category;
		this.bugTrackerRef = bugTrackerRef;
	}

	public boolean match(LoggingEvent event) {
		if (matchLevel(event) && matchLogger(event) && matchThrowable(event)
				&& matchMessage(event)) {
			return true;
		}
		return false;
	}

	private boolean matchThrowable(LoggingEvent event) {
		if (this.throwablePattern == null) {
			return true;
		}
		if (event.getThrowableInformation() == null) {
			return false;
		}
		StringBuilder throwable = new StringBuilder();
		for (String line : event.getThrowableStrRep()) {
			throwable.append(line);
		}
		boolean result = this.throwablePattern.matcher(throwable.toString())
				.matches();
		return result;
	}

	private boolean matchMessage(LoggingEvent event) {
		if (this.messagePattern == null) {
			return true;
		}
		if (event.getMessage() == null) {
			return false;
		}
		boolean result = this.messagePattern.matcher(
				event.getMessage().toString()).matches();
		return result;
	}

	private boolean matchLogger(LoggingEvent event) {
		if (this.loggerName == null) {
			return true;
		}
		return this.loggerName.equals(event.getLoggerName());
	}

	private boolean matchLevel(LoggingEvent event) {
		if (this.level == null) {
			return true;
		}
		boolean result = this.level.equals(event.getLevel());
		return result;
	}

	public String getCategory() {
		return this.category;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("description", this.description)
				.append("loggerName", this.loggerName)
				.append("level", this.level)
				.append("messagePattern",
						this.messagePattern != null ? this.messagePattern
								.pattern() : (String) null)
				.append("throwablePattern",
						this.throwablePattern != null ? this.throwablePattern
								.pattern() : (String) null)
				.append("category", this.category)
				.append("bugTrackerRef", this.bugTrackerRef).toString();
	}
}
