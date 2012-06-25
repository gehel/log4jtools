package ch.ledcom.log4jtools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import ch.ledcom.log4jtools.config.ConfigReader;
import ch.ledcom.log4jtools.filter.CategorizationFilter;
import ch.ledcom.log4jtools.processor.CategorizingProcessor;
import ch.ledcom.log4jtools.processor.LogProcessor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;

public class Log4JXMLCategorizer {

	public static void main(String[] args) throws IOException,
			InvalidFormatException {

		JCOptions options = new JCOptions();
		new JCommander(options, args);

		List<CategorizationFilter> filters = readConfig(options.getConfigFile());

		CategorizingProcessor processor = new CategorizingProcessor(filters,
				initAppenders(options.getOutputDirectory(), filters));

		List<LogProcessor> processors = new ArrayList<LogProcessor>();
		processors.add(processor);

		LogFileXMLReader reader = new LogFileXMLReader(processors);

		if (options.getInputFiles().size() == 0) {
			reader.process(
					new BufferedReader(new InputStreamReader(System.in)),
					options.getHost(), options.getApplication());
		} else {
			for (File f : getLogFiles(options.getInputFiles())) {
				BufferedReader in = new BufferedReader(new FileReader(f));
				try {
					reader.process(in, options.getHost(),
							options.getApplication());
				} finally {
					Closeables.closeQuietly(in);
				}
			}
		}
	}

	private static List<File> getLogFiles(List<File> files) {
		List<File> result = new ArrayList<File>();
		for (File file : files) {
			if (file.isDirectory()) {
				result.addAll(getLogFiles(file.listFiles()));
			} else {
				result.add(file);
			}
		}
		return result;
	}
	
	private static List<File> getLogFiles(File[] files) {
		return getLogFiles(Arrays.asList(files));
	}
	
	private static List<CategorizationFilter> readConfig(File configFile)
			throws InvalidFormatException, IOException {

		ConfigReader configReader = new ConfigReader();

		InputStream in = new FileInputStream(configFile);
		List<CategorizationFilter> filters;
		try {
			filters = configReader.loadConfig(in);
		} finally {
			Closeables.closeQuietly(in);
		}
		for (CategorizationFilter filter : filters) {
			System.out.println(filter);
		}

		return filters;
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

	private static final class JCOptions {
		@Parameter(description = "files to parse", converter = FileConverter.class, required = true)
		private List<File> inputFiles;

		@Parameter(names = "-o", converter = FileConverter.class, required = true)
		private File outputDirectory;

		@Parameter(names = { "-c", "-config" }, converter = FileConverter.class, required = true)
		private File configFile;

		@Parameter(names = "-host", required = true)
		private String host;

		@Parameter(names = "-application", required = true)
		private String application;

		public List<File> getInputFiles() {
			return inputFiles;
		}

		public File getOutputDirectory() {
			return outputDirectory;
		}

		public File getConfigFile() {
			return configFile;
		}

		public String getHost() {
			return host;
		}

		public String getApplication() {
			return application;
		}

	}

}
