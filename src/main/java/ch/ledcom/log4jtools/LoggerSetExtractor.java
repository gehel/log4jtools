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
