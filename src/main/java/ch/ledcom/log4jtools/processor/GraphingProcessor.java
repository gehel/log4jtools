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
package ch.ledcom.log4jtools.processor;

import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.ConsolFun.MAX;
import static org.rrd4j.DsType.ABSOLUTE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;
import org.rrd4j.core.DsDef;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

import com.google.common.collect.ImmutableList;

public class GraphingProcessor implements LogProcessor {

	private final List<String> categories;

	private final RrdDb rrdDb;

	private Date firstSample = null;
	private Date lastSample = null;
	private Date lastRotation = null;

	private static final long ROTATION_DELAY = 300 * 1000;

	private final Map<String, Integer> consolidation = new HashMap<String, Integer>();

	public GraphingProcessor(List<String> categories, String rrdPath)
			throws IOException {
		this.categories = ImmutableList.copyOf(categories);
		this.rrdDb = new RrdDb(createRrdDef(rrdPath));
	}

	private RrdDef createRrdDef(String rrdPath) {
		RrdDef rrdDef = new RrdDef(rrdPath, 300);
		rrdDef.addArchive(AVERAGE, 0.5, 1, 600);
		rrdDef.addArchive(MAX, 0.5, 1, 600);
		for (String category : this.categories) {
			rrdDef.addDatasource(new DsDef(category, ABSOLUTE, 50, Double.NaN,
					Double.NaN));
		}
		rrdDef.addDatasource(new DsDef("default", ABSOLUTE, 50, Double.NaN,
				Double.NaN));
		return rrdDef;
	}

	@Override
	public void process(LoggingEvent event) throws IOException {
		if (this.firstSample == null) {
			this.firstSample = new Date(event.getTimeStamp());
			this.lastRotation = this.firstSample;
			this.lastSample = this.firstSample;
		}
		if (this.lastSample.getTime() > event.getTimeStamp()) {
			return;
		}
		if (this.lastRotation.getTime() + ROTATION_DELAY < event.getTimeStamp()) {
			rotate(new Date(event.getTimeStamp()));
		}

		increment(findCategory(event));
		this.lastSample = new Date(event.getTimeStamp());
	}

	private void increment(String category) {
		if (!this.consolidation.containsKey(category)) {
			this.consolidation.put(category, 0);
		}
		Integer counter = this.consolidation.get(category);
		this.consolidation.put(category, counter + 1);
	}

	private void rotate(Date date) throws IOException {
		for (String category : categories) {
			Sample sample = this.rrdDb.createSample();
			sample.setTime(this.lastRotation.getTime());
			if (consolidation.containsKey(category)) {
				sample.setValue(category, consolidation.get(category));
			} else {
				sample.setValue(category, 0);
			}
			sample.update();
		}
		consolidation.clear();
		this.lastRotation = date;
	}

	private String findCategory(LoggingEvent event) {
		return findCategory(event.getLoggerName());
	}

	private String findCategory(String logger) {
		for (String category : this.categories) {
			if (logger.startsWith(category)) {
				return category;
			}
		}
		return "default";
	}

	public Date getFirstSample() {
		return this.firstSample;
	}

	public Date getLastSample() {
		return this.lastSample;
	}

	public void close() throws IOException {
		this.rrdDb.close();
	}

}
