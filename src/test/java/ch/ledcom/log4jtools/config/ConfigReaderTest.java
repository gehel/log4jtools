package ch.ledcom.log4jtools.config;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import ch.ledcom.log4jtools.filter.CategorizationFilter;

public class ConfigReaderTest {

	@Test
	public void testRead() throws InvalidFormatException, IOException {
		ConfigReader reader = new ConfigReader();
		InputStream in = ConfigReaderTest.class.getClassLoader()
				.getResourceAsStream("categories.xls");
		List<CategorizationFilter> filters = reader.loadConfig(in);
		assertEquals(1, filters.size());
		CategorizationFilter filter1 = filters.get(0);
		assertEquals("test1", filter1.getCategory());
		assertEquals("test 1", filter1.getDescription());
	}
}
