package org.soky.sro.pk2.entityextractors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.soky.sro.pk2.IPk2File;

public class NamesExtracor implements IPK2EntityExtractor<Map<String, String>> {

	private IPk2File reader;

	public NamesExtracor(IPk2File reader) {

		this.reader = reader;
	}

	@Override
	public Map<String, String> extract() {

		Map<String, String> res = new HashMap<>();

		String fileNames[] = getFileNames();

		for (String fileName : fileNames) {

			res.putAll(extractNamesAt(fileName));

		}

		return res;
	}

	private Map<String, String> extractNamesAt(String entryName) {

		Map<String, String> res = new HashMap<>();
		List<JMXFile> matchedFiles = this.reader.find("(?i)" + entryName);
		for (JMXFile file : matchedFiles) {
			byte[] fileBytes = this.reader.getFileBytes(file);

			if (fileBytes != null && fileBytes.length > 0) {
				try {

					InputStream input = new ByteArrayInputStream(fileBytes);
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(input, "UTF-16"));

					Iterable<CSVRecord> records = CSVFormat.MYSQL.builder().setTrim(true).build().parse(inputReader);

					for (CSVRecord record : records) {

						if (record.size() < 2 || record.hasComment())
							continue;

						String firstField = record.get(0);

						if (firstField.startsWith("//") || firstField.equals("0") || firstField.isBlank())
							continue;

						String key = record.get(1);

						String value = null;

						for (int i = 2; i < record.size(); i++) {

							if (Pattern.matches(".*[a-zA-Z]+.*", record.get(i))) {
								value = record.get(i);
								break;

							}

						}
						if (value == null)
							continue;

						res.put(key, value);

					}

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return res;
	}

	private String[] getFileNames() {

		List<JMXFile> txtDataNames = this.reader.find("(?i)textdataname.txt");
		List<String> res = new ArrayList<>();

		for (JMXFile textDataNameFile : txtDataNames) {

			byte[] fileBytes = this.reader.getFileBytes(textDataNameFile);

			String fileStr = new String(fileBytes, Charset.forName("UTF-16"));
			String[] files = fileStr.split(System.getProperty("line.separator"));
			for (String file : files) {
				res.add(file);
			}
		}
		return res.toArray(new String[0]) ; 

	}
}
