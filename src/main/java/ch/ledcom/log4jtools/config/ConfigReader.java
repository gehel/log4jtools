package ch.ledcom.log4jtools.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import ch.ledcom.log4jtools.filter.CategorizationFilter;

public class ConfigReader {

	public List<CategorizationFilter> loadConfig(InputStream in)
			throws InvalidFormatException, IOException {
		List<CategorizationFilter> result = new ArrayList<CategorizationFilter>();
		Workbook wb = WorkbookFactory.create(in);
		Sheet sheet = wb.getSheetAt(0);
		int i = 1;
		while (true) {
			Row row = sheet.getRow(i++);

			String description = readCell(row, 0);
			if (description == null) {
				break;
			}
			String loggerName = readCell(row, 1);
			Level level = readCell(row, 2) == null ? (Level) null : Level
					.toLevel(readCell(row, 2));
			Pattern messagePattern = readPattern(row, 3);
			Pattern throwablePattern = readPattern(row, 4);
			String category = readCell(row, 5);
			String bugTrackerRef = readCell(row, 6);

			CategorizationFilter filter = new CategorizationFilter(description,
					loggerName, level, messagePattern, throwablePattern,
					category, bugTrackerRef);
			result.add(filter);
		}
		return result;
	}
	
	private Pattern readPattern(Row row, int cellnum) {
		String cell = readCell(row, cellnum);
		if (cell == null || cell.equals("")) {
			return null;
		}
		return Pattern.compile(cell, Pattern.DOTALL);
	}

	private String readCell(Row row, int cellnum) {
		if (row == null) {
			return null;
		}
		Cell cell = row.getCell(cellnum);
		return cell == null ? (String) null : cell.getStringCellValue().trim();
	}

}
