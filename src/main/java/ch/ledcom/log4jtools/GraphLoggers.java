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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import static org.rrd4j.ConsolFun.AVERAGE;

import ch.ledcom.log4jtools.processor.GraphingProcessor;
import ch.ledcom.log4jtools.processor.LogProcessor;

import com.google.common.collect.ImmutableList;

public class GraphLoggers {

    /**
     * Make sure class is never instantiated.
     */
    private GraphLoggers() {
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {

        List<String> categories = ImmutableList.of("com", "org");

        File logFile = new File("/home/glederre/logs/problems.xml");
        LoggerSetExtractor loggerSetProcessor = new LoggerSetExtractor();
        GraphingProcessor graphingProcessor = new GraphingProcessor(categories,
                "/home/glederre/logs/rrd/test");

        List<LogProcessor> processors = new ArrayList<LogProcessor>();
        processors.add(loggerSetProcessor);
        processors.add(graphingProcessor);

        LogFileXMLReader reader = new LogFileXMLReader(processors);

        reader.process(new BufferedReader(new FileReader(logFile)), "hqhcecom",
                "prod");

        graphingProcessor.close();

        Set<String> loggers = loggerSetProcessor.getLoggers();
        for (String logger : loggers) {
            System.out.println(logger);
        }

        RrdGraphDef gDef = new RrdGraphDef();
        gDef.setWidth(500);
        gDef.setHeight(300);
        gDef.setFilename("/home/glederre/logs/test.png");
        gDef.setStartTime(graphingProcessor.getFirstSample().getTime());
        gDef.setEndTime(graphingProcessor.getLastSample().getTime());
        gDef.setTitle("My Title");
        gDef.setVerticalLabel("occ");

        for (String category : categories) {
            gDef.datasource("occ", "/home/glederre/logs/rrd/test", category,
                    AVERAGE);
        }
        gDef.hrule(2568, Color.GREEN, "hrule");
        gDef.setImageFormat("png");
        RrdGraph graph = new RrdGraph(gDef);
    }
}
