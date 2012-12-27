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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import ch.ledcom.log4jtools.log4j.spi.Decoder;
import org.apache.log4j.spi.LoggingEvent;
import ch.ledcom.log4jtools.log4j.xml.XMLDecoder;

import ch.ledcom.log4jtools.processor.LogProcessor;

import com.google.common.collect.ImmutableList;

public class LogFileXMLReader {

    private final Decoder decoder;

    private final List<LogProcessor> logProcessors;

    private static final String HOSTNAME_KEY = "hostname";
    private static final String APPLICATION_KEY = "application";

    public LogFileXMLReader(List<LogProcessor> logProcessors)
            throws IOException {
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
            if (evt.getProperty(HOSTNAME_KEY) == null) {
                evt.setProperty(HOSTNAME_KEY, host);
            }
            if (evt.getProperty(APPLICATION_KEY) == null) {
                evt.setProperty(APPLICATION_KEY, application);
            }

            for (LogProcessor processor : this.logProcessors) {
                processor.process(evt);
            }
        }
    }

}
