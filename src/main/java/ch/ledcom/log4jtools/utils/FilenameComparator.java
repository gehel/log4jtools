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
package ch.ledcom.log4jtools.utils;

import java.io.File;
import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;

public class FilenameComparator implements Comparator<File> {
	@Override
	public final int compare(File f1, File f2) {
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
