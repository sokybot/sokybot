package org.sokybot.pk2extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.sokybot.pk2.JMXFile;
import org.sokybot.pk2extractor.exception.Pk2InvalidResourceFormatException;

import static org.sokybot.pk2.io.Pk2IO.getInputStream;
import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toBufferedReader;

/**
 * This class is intended for internal use only
 * 
 * @author Amr
 *
 */
public class Pk2ExtractorUtils {

	static byte[] toByteArray(JMXFile jmx) throws Pk2InvalidResourceFormatException {
		try (InputStream in = getInputStream(jmx)) {

			return IOUtils.toByteArray(in);

		} catch (IOException e) {
			// TODO write appropriate message
			throw new Pk2InvalidResourceFormatException("Could not ", jmx.getName(), e);
		}

	}

	static String toString(JMXFile jmx) throws Pk2InvalidResourceFormatException {
		try {
			return new String(toByteArray(jmx), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO write appropriate message
			throw new Pk2InvalidResourceFormatException("", jmx.getName(), e);
		}
	}

	static BufferedReader toBufferedReader(JMXFile jmx) {
		return new BufferedReader(new InputStreamReader(getInputStream(jmx), StandardCharsets.UTF_16LE));
	}

	static Stream<CSVRecord> toCSVRecordStream(JMXFile jmx) throws Pk2InvalidResourceFormatException {
		try {
			return CSVFormat.MYSQL.builder().setIgnoreEmptyLines(true).setTrim(true).setCommentMarker('/').build()
					.parse(toBufferedReader(jmx)).stream();
		} catch (IOException e) {

			throw new Pk2InvalidResourceFormatException("", jmx.getName(), e);
		}
	}

}
