package org.sokybot.pk2extractor;

import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.function.Failable;
import org.apache.commons.lang3.math.NumberUtils;
import org.sokybot.domain.Gender;
import org.sokybot.domain.Race;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.domain.items.ItemType;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;
import org.sokybot.pk2extractor.exception.Pk2MissedResourceException;

import static org.sokybot.pk2extractor.Pk2ExtractorUtils.*;

public class ItemEntityExtractor implements IPK2EntityExtractor<ItemEntity> {


	
	@Override
	public Stream<ItemEntity> extract(IPk2File pk2File) throws Pk2ExtractionException {
		return toBufferedReader(
				pk2File.findFirst("itemdata.txt")
				.orElseThrow(() -> new Pk2MissedResourceException(null, null, null) ))
				.lines()
				.flatMap((itemFileName) -> pk2File.find("(?i)" + itemFileName).stream())
				.flatMap(Failable.asFunction(Pk2ExtractorUtils::toCSVRecordStream))
				.filter((record)->record.size() > 57 && !record.get(0).startsWith("//"))
				.map(this::toItemEntity)
				.distinct() ;
		
		
				//.flatMap(this::extractItems);

	}
  
	public static void main(String args[]) { 
		ItemEntityExtractor ex = new ItemEntityExtractor() ; 
		try {
		 long count = 	ex.extract(IPk2File.open("E:\\Amroo\\Silkroad Games\\LegionSRO_15_08_2019\\Media.pk2"))
			.peek((itemEntity)->System.out.println(itemEntity)).count();
		 System.out.println(count ) ; 
		} catch (Pk2ExtractionException e) {
			System.out.println(e.getMessage()) ; 
		}
	}
	
	private ItemEntity toItemEntity(CSVRecord record) { 
		ItemEntity entity = new ItemEntity();
		String field = record.get(1); // id

		if (NumberUtils.isParsable(field)) {
			entity.setId(Integer.parseInt(field));
		} 

		field = record.get(2); // long id

		if (!field.isBlank()) {
			entity.setLongId(field);
		}
		
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
		} 

		field = record.get(11); // type byte 2

		if (NumberUtils.isParsable(field)) {
			type += Integer.toHexString(Integer.parseInt(field));
		} 

		field = record.get(12); // type byte 3

		if (NumberUtils.isParsable(field)) {
			type += Integer.toHexString(Integer.parseInt(field));
		} 

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
			if (NumberUtils.isParsable(field)) {
				itemGender = Gender.parseType(Byte.valueOf(field));
			}
		}

		entity.setGender(itemGender);

		int degree = 0;

		if (record.size() > 61) {
			field = record.get(61);
			if (NumberUtils.isParsable(field)) {
				degree = (Integer.parseInt(field) + 3 - 1) / 3;
			}
		}

		entity.setDegree(degree);

		return entity ; 
		
	}
	

}

