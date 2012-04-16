package ch.ledcom.log4jtools.utils;

import java.io.File;
import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;

public class FilenameComparator implements Comparator<File> {
	@Override
	public int compare(File f1, File f2) {
		DateTime d1 = ParseUtils.extractDateFromName(f1);
		Integer s1 = ParseUtils.extractSequenceFromName(f1);
		DateTime d2 = ParseUtils.extractDateFromName(f2);
		Integer s2 = ParseUtils.extractSequenceFromName(f2);

		if (ObjectUtils.compare(d1, d2, true) == 0) {
			return ObjectUtils.compare(s1, s2, true);
		}
		return ObjectUtils.compare(d1, d2, true);
	}

}
