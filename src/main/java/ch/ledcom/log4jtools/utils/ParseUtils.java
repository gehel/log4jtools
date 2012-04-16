package ch.ledcom.log4jtools.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public final class ParseUtils {

	private static final Pattern DATE_PATTERN = Pattern
			.compile(".*(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d).(\\d+)");

	private ParseUtils() {
	}

	public static DateTime extractDateFromName(File file) {
		Matcher m = DATE_PATTERN.matcher(file.getName());
		if (!m.matches()) {
			return null;
		}
		int year = Integer.parseInt(m.group(1));
		int month = Integer.parseInt(m.group(2));
		int day = Integer.parseInt(m.group(3));
		return new DateTime(year, month, day, 0, 0);
	}

	public static Integer extractSequenceFromName(File file) {
		Matcher m = DATE_PATTERN.matcher(file.getName());
		if (!m.matches()) {
			return null;
		}
		return Integer.valueOf(m.group(4));
	}
}
