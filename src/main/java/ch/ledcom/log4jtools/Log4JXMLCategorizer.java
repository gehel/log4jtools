package ch.ledcom.log4jtools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.xml.XMLLayout;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import ch.ledcom.log4jtools.config.ConfigReader;
import ch.ledcom.log4jtools.filter.CategorizationFilter;
import ch.ledcom.log4jtools.processor.CategorizingProcessor;
import ch.ledcom.log4jtools.processor.LogProcessor;
import ch.ledcom.log4jtools.utils.FilenameComparator;

public class Log4JXMLCategorizer {

	private final LogFileXMLReader logReader;

	public Log4JXMLCategorizer(LogFileXMLReader logReader) {
		this.logReader = logReader;
	}

	private void processLogDirectory(File directory, String host, String path)
			throws IOException {
		if (!directory.exists() || !directory.isDirectory()) {
			throw new IllegalArgumentException(
					"Given argument is not a directory : "
							+ directory.getAbsolutePath());
		}
		System.out.println("processing directory : "
				+ directory.getAbsolutePath());
		File[] children = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile()
						&& pathname.getAbsolutePath().contains("xml");
			}
		});

		// sort files to process them by date
		Arrays.sort(children, new FilenameComparator());

		// process logs
		for (File child : children) {
			System.out.println("processing : " + child.getAbsolutePath());
			processLog(child, host, path);
		}
	}

	private void processLog(File xmlLogFile, String host, String path)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(xmlLogFile));
		this.logReader.process(reader, host, path);
	}

	public static void main(String[] args) throws IOException,
			InvalidFormatException {
		File baseLogDirectory = new File(args[0]);
		String env = args[1];
		String serversArg = args[2];

		System.out.println("env : " + env);
		System.out.println("servers : " + serversArg);

		String[] servers = serversArg.split(",");

		File envDirectory = new File(baseLogDirectory, env);

		ConfigReader configReader = new ConfigReader();
		InputStream in = LogFileXMLReader.class.getClassLoader()
				.getResourceAsStream("categorization.xls");
		List<CategorizationFilter> filters = configReader.loadConfig(in);

		for (CategorizationFilter filter : filters) {
			System.out.println(filter);
		}

		CategorizingProcessor processor = new CategorizingProcessor(filters,
				initAppenders(new File("/home/glederre/logs"), filters));

		List<LogProcessor> processors = new ArrayList<LogProcessor>();
		processors.add(processor);

		Log4JXMLCategorizer categorizer = new Log4JXMLCategorizer(
				new LogFileXMLReader(processors));

		for (String server : servers) {
			File logDirectory = new File(envDirectory, server);
			categorizer.processLogDirectory(logDirectory, server, env);
		}
	}

	private static Map<String, Appender> initAppenders(File baseOutputDir,
			Collection<CategorizationFilter> filters) throws IOException {
		Map<String, Appender> result = new HashMap<String, Appender>();

		for (CategorizationFilter filter : filters) {
			if (result.containsKey(filter.getCategory())
					|| Strings.isNullOrEmpty(filter.getCategory())) {
				continue;
			}

			XMLLayout layout = new XMLLayout();
			layout.setLocationInfo(true);
			layout.setProperties(true);
			FileAppender appender = new FileAppender();
			appender.setFile(new File(baseOutputDir, filter.getCategory()
					+ ".xml").getCanonicalPath());
			appender.setLayout(layout);
			appender.activateOptions();
			appender.setAppend(false);

			result.put(filter.getCategory(), appender);
		}

		return ImmutableMap.copyOf(result);
	}

}
