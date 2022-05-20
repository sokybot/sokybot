package org.soky.sro.pk2.entityextractors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.soky.sro.pk2.IPk2File;

//
//import sokybot.bot.trainermodel.Gender;
//import sokybot.bot.trainermodel.Race;
//import sokybot.gamemodel.ItemEntity;
//import sokybot.gamemodel.ItemType;
//import sokybot.pk2.IMediaPk2;
//import sokybot.pk2.IPk2Reader;
//import sokybot.pk2.JMXFile;

public class ItemEntityExtractor implements IPK2EntityExtractor<Set<ItemEntity>> {

	private IPk2File pk2File;

	public ItemEntityExtractor(IPk2File pk2Reader) {
		this.pk2File = pk2Reader;
	}

	@Override
	public Set<ItemEntity> extract() {
		Set<ItemEntity> res = new HashSet<>();

		String[] itemFiles = extractItemFileNames();

		if (itemFiles == null || itemFiles.length == 0)
			return null;

		try {
			for (int i = 0; i < itemFiles.length; i++) {

				Set<ItemEntity> extractedItems = extractItemDataAt(itemFiles[i]);
				if (extractedItems != null) {
					res.addAll(extractedItems);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	private String[] extractItemFileNames() {

		byte[] fileBytes = this.pk2File.getFileBytes("itemdata.txt");
		String fileStr = new String(fileBytes, Charset.forName("UTF-16"));
		return fileStr.split(System.getProperty("line.separator"));

	}


	private Set<ItemEntity> extractItemDataAt(String fileName) throws IOException {

		List<JMXFile> matchedFiles = this.pk2File.find("(?i)" + fileName );

		Set<ItemEntity> res = new HashSet<>();
		
		
		for(JMXFile file : matchedFiles ) { 
		

			byte[] fileBytes = this.pk2File.getFileBytes(file);

			if (fileBytes != null && fileBytes.length > 0) {

				InputStream in = new ByteArrayInputStream(fileBytes);
				BufferedReader inReader = new BufferedReader(new InputStreamReader(in, "UTF-16"));

				Iterable<CSVRecord> records = CSVFormat.MYSQL.parse(inReader);

				for (CSVRecord record : records) {

					if (record.size() < 1 || record.size() < 58)
						continue;
					if (record.get(0).startsWith("//"))
						continue;

					ItemEntity entity = new ItemEntity();
					String field = record.get(1); // id

					if (NumberUtils.isParsable(field)) {
						entity.setId(Integer.parseInt(field));
					} else
						continue;

					field = record.get(2); // long id

					if (!field.isBlank()) {
						entity.setLongId(field);
					} else
						continue;

					field = record.get(5); // name
					entity.setName(field);

					field = record.get(7); // isMallItem {0 , 1}
					boolean isMall = false;
					if (NumberUtils.isParsable(field)) {
						isMall = BooleanUtils.toBoolean(Byte.valueOf(field));
					}
					entity.setMallItem(isMall);

					String type = "";

					field = record.get(10); // type byte 1

					if (NumberUtils.isParsable(field)) {
						type += Integer.toHexString(Integer.parseInt(field));
					} else
						continue;

					field = record.get(11); // type byte 2

					if (NumberUtils.isParsable(field)) {
						type += Integer.toHexString(Integer.parseInt(field));
					} else
						continue;

					field = record.get(12); // type byte 3

					if (NumberUtils.isParsable(field)) {
						type += Integer.toHexString(Integer.parseInt(field));
					} else
						continue;

					entity.setItemType(ItemType.parseType(Integer.valueOf(type, 16), record.get(2)));

					field = record.get(14); // item race
					Race itemRace = Race.Universal;

					if (NumberUtils.isParsable(field)) {
						itemRace = Race.parseType(Byte.valueOf(field));
					}
					entity.setRace(itemRace);

					field = record.get(15); // isSOX
					entity.setSOX(field.equals("1"));

					field = record.get(19);
					entity.setSortable(!field.equals("0"));

					field = record.get(33); // lvl
					int lvl = 0;
					if (NumberUtils.isParsable(field)) {
						lvl = Integer.parseInt(field);
					}
					entity.setLevel(lvl);

					field = record.get(57);
					int maxStacks = 0;
					if (NumberUtils.isParsable(field)) {
						maxStacks = Integer.parseInt(field);
					}
					entity.setMaxStacks(maxStacks);

					Gender itemGender = Gender.Unisex;

					if (record.size() > 58) {
						field = record.get(58);
						if(NumberUtils.isParsable(field)) { 
							itemGender = Gender.parseType(Byte.valueOf(field)) ; 
						}
					}
					
					entity.setGender(itemGender);
                    
					int degree = 0 ; 
					
					if(record.size() > 61) { 
						field = record.get(61) ; 
						if(NumberUtils.isParsable(field)) { 
							degree = ( Integer.parseInt(field) + 3 -1 ) / 3 ;  
						}
					}
//					options.add(String.valueOf(degree));

					entity.setDegree(degree);

					res.add(entity) ; 
				}
			}
		}
		
		return res;
	}


}
