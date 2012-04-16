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
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		List<String> categories = ImmutableList.of("com", "org");
		
		File logFile = new File("/home/glederre/logs/problems.xml");
		LoggerSetExtractor loggerSetProcessor = new LoggerSetExtractor();
		GraphingProcessor graphingProcessor = new GraphingProcessor(categories, "/home/glederre/logs/rrd/test");

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
			gDef.datasource("occ", "/home/glederre/logs/rrd/test", category, AVERAGE);
		}
		gDef.hrule(2568, Color.GREEN, "hrule");
		gDef.setImageFormat("png");
		RrdGraph graph = new RrdGraph(gDef);
	}
}
