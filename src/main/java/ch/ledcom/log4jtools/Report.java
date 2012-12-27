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
