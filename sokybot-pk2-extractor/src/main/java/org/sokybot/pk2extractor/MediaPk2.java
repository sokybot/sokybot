/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.pk2extractor;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Failable;
import org.apache.commons.lang3.math.NumberUtils;
import org.sokybot.domain.Division;
import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.Gender;
import org.sokybot.domain.Race;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.SkillType;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.domain.items.ItemType;
import org.sokybot.pk2.IPk2File;

import org.sokybot.pk2extractor.exception.Pk2InvalidResourceFormatException;
import org.sokybot.pk2extractor.exception.Pk2MissedResourceException;
import org.sokybot.security.Blowfish;

import lombok.extern.slf4j.Slf4j;

import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toByteArray;

/**
 *
 * @author AMROO
 */
@Slf4j
public class MediaPk2 implements IMediaPk2 {

	private IPk2File mediaFile;
	private Runnable onCloseAction;

	private Map<String, String> names;

	public MediaPk2(IPk2File mediaFile, Runnable onCloseAction) {
		this.mediaFile = mediaFile;
		this.onCloseAction = onCloseAction;

	}

	private void checkNames() {
		if (this.names == null) {
			this.names = getEntityNames();
		}

	}

	private Map<String, String> getEntityNames() {
		log.info("extracting entity names from media pk2 file ");
		return this.mediaFile.find("((?i)(textdata_equip&skill(_\\d*?)?(\\.txt))|(textdata_object(_\\d*?)?(\\.txt)))")
				.stream()
				.flatMap(Pk2ExtractorUtils::toCSVRecordStream)
				.filter((record) -> {
					if (record.size() < 2 || record.hasComment())
						return false;

					String firstField = record.get(0);

					return !firstField.startsWith("//") && !firstField.equals("0") && !firstField.isBlank();
				})
				.collect(Collectors.toMap((record) -> {

					for (int i = 1; i < record.size(); i++) {
						if (record.get(i).contains("SN_")) {
							return record.get(i);
						}
					}
					return "UNDEFINED";

				}, (record) -> {

					for (int i = 2; i < record.size(); i++) {
						String val = record.get(i);

						if (!val.contains("SN_") && Pattern.matches(".*[a-zA-Z]+.*", val)) {
							return record.get(i);

						}

					}

					return "UNDEFINED";

				}, (name1, name2) -> {

					return name2;
				}));

	}

	/*
	 * private Map<String, String> getEntityNames2() {
	 * 
	 * return this.mediaFile.find("(?i)textdataname.txt") .stream() .peek((jmx) ->
	 * System.out.println(jmx.getName())) .map(jmxFile -> toBufferedReader(jmxFile))
	 * .flatMap(BufferedReader::lines) .peek((l) ->
	 * System.out.println("File To read From : " + l)) .flatMap((fileEntryName) ->
	 * this.mediaFile.find("(?i)" + fileEntryName).stream())
	 * .flatMap(Pk2ExtractorUtils::toCSVRecordStream) .filter((record) -> { if
	 * (record.size() < 2 || record.hasComment()) return false;
	 * 
	 * String firstField = record.get(0);
	 * 
	 * return !firstField.startsWith("//") && !firstField.equals("0") &&
	 * !firstField.isBlank(); }) .collect(Collectors.toMap((record) ->
	 * record.get(1), (record) -> {
	 * 
	 * for (int i = 2; i < record.size(); i++) {
	 * 
	 * if (Pattern.matches(".*[a-zA-Z]+.*", record.get(i))) { return record.get(i);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return "UNDEFINED";
	 * 
	 * }, (name1, name2) -> {
	 * 
	 * return name2; }));
	 * 
	 * }
	 */
	@Override
	public Stream<ItemEntity> getItemEntities() {
		checkNames();
		log.debug("extracting item entities from media.pk2 file");
		return this.mediaFile.findFirst("itemdata.txt")
				.map(jmx -> Pk2ExtractorUtils.toString(jmx, StandardCharsets.UTF_16LE.name()))
				.map(Pk2ExtractorUtils::toLines)
				.orElseThrow(() -> new Pk2MissedResourceException("Colud not find itemdata.txt file", "itemdata.txt"))
				.flatMap((itemFileName) -> this.mediaFile.find("(?i)" + itemFileName).stream())
				.flatMap(Pk2ExtractorUtils::toCSVRecordStream)
				.filter((record) -> record.size() > 57 && !record.get(0).startsWith("//"))
				.map(this::toItemEntity)
				.distinct()
				.peek((item) -> item.setName(names.get(item.getName())));

	}

	@Override
	public Stream<SkillEntity> getSkillEntities() {
		checkNames();
		log.debug("extracting skill entities from media.pk2 file");
		return  this.mediaFile.find("skilldata_(\\d+)(enc)?.txt$")
			       .stream()
			       .flatMap(Pk2ExtractorUtils::toCSVRecordStream)
			       .filter((r)->r.size() > 70)
			       .map(this::toSkillEntity)
			       .distinct();

	}

	@Override
	public void close() throws IOException {
		log.debug("close channel with media.pk2 file");
		this.mediaFile.close();
		this.onCloseAction.run();
	}

	@Override
	public int extractVersion() {
		log.debug("extracting version from media.pk2 file");
		return this.mediaFile.findFirst("SV.T")
				.map(Pk2ExtractorUtils::firstChunk)
				.map((bytes) -> Blowfish.newInstance("SILKROAD".getBytes()).decode(0, bytes))
				.map(String::new)
				.map(String::trim)
				.map(Pk2ExtractorUtils::toInteger)
				.orElseThrow(() -> new Pk2MissedResourceException("Colud not find SV.T file ", "SV.T"));

	}

	@Override
	public int extractPort() {
		log.debug("extracting port from media.pk2 file");
		return this.mediaFile.findFirst("(?i)gateport.txt")
				.map(Pk2ExtractorUtils::toText)
				.map(String::trim)
				.map(Pk2ExtractorUtils::toInteger)
				.orElseThrow(() -> new Pk2MissedResourceException("Could not find gateport.txt file ", "gateport.txt"));

	}

	private String nextString(ByteBuffer buffer) {

		try {
			byte[] strBuffer = new byte[buffer.getInt()];
			buffer.get(strBuffer);
			return new String(strBuffer);
		} catch (IndexOutOfBoundsException | BufferUnderflowException ex) {
			throw new Pk2InvalidResourceFormatException("An error occurred while parsing a joymax resource file ", ex);
		}

	}

	private byte nextByte(ByteBuffer buffer) {
		try {
			return buffer.get();
		} catch (BufferUnderflowException ex) {
			throw new Pk2InvalidResourceFormatException("An error occurred while parsing a joymax resource file ", ex);
		}
	}

	@Override
	public DivisionInfo extractDivisionInfo() {

		log.debug("extracting division information from media.pk2 file ");
		return this.mediaFile.findFirst("(?i)divisioninfo.txt").map((jmxFile) -> {
			DivisionInfo divInfo = new DivisionInfo();
			ByteBuffer buffer = ByteBuffer.wrap(toByteArray(jmxFile)).order(ByteOrder.LITTLE_ENDIAN);
			divInfo.local = nextByte(buffer);
			byte divCount = nextByte(buffer);

			Stream.generate(Division::new).limit(divCount).forEach((div) -> {
				div.name = nextString(buffer);
				nextByte(buffer); // skip 1 byte
				byte ipCount = nextByte(buffer);
				for (int i = 0; i < ipCount; i++) {
					div.addHost(nextString(buffer));
					nextByte(buffer); // skip 1 byte
				}
				divInfo.addDivision(div);
			});

			return divInfo;

		})
				.orElseThrow(() -> new Pk2MissedResourceException("Could not find Media.pk2$divisioninfo.txt file  ",
						"divisioninfo.txt"));

	}

	@Override
	public SilkroadType extractType() {
		log.debug("extracting game type info from media.pk2 file");
		return this.mediaFile.findFirst("type.txt")
				.map(Pk2ExtractorUtils::toText)
				.map(Pk2ExtractorUtils::toLines)
				.map((lines) -> {
					
					Map<String, String> props = new HashMap<>() ; 
					AtomicBoolean isEmpty = new AtomicBoolean(true);
					lines.filter(line -> line.contains("="))

							.forEach((line) -> {
								String parts[] = StringUtils.split(line, "=");
								props.put(parts[0].trim(), parts[1].replace("\"", " ").trim());
								isEmpty.set(false);
							});

					if (isEmpty.get()) {
						throw new Pk2InvalidResourceFormatException("Unexpected format for joymax file type.txt",
								"type.txt");
					}

					return  new SilkroadType(props);
				})
				.orElseThrow(() -> new Pk2MissedResourceException("Could not find joymax file  type.txt", "type.txt"));

	}

	private ItemEntity toItemEntity(CSVRecord record) {
		ItemEntity entity = new ItemEntity();
		String field = record.get(1); // id

		if (NumberUtils.isParsable(field)) {
			entity.setRefId(Integer.parseInt(field));
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

		return entity;

	}
	private SkillEntity toSkillEntity(CSVRecord record) { 
		SkillEntity skill = new SkillEntity();

		String field = record.get(1);
		if (NumberUtils.isParsable(field)) {

			skill.setRefId(Integer.parseInt(field));
		}

		skill.setLongId(record.get(3));


		field = record.get(13); // castTime
		if (NumberUtils.isParsable(field)) {
			skill.setCastTime(Integer.parseInt(field));
		}

		field = record.get(14); // cooldown
		if (NumberUtils.isParsable(field)) {
			skill.setCooldown(Integer.parseInt(field));
		}

		field = record.get(22);
		boolean targetRequired = false;
		
		if (NumberUtils.isParsable(field)) {
			targetRequired = BooleanUtils.toBoolean(Byte.valueOf(field));
		}
		skill.setTargetRequired(targetRequired);
		
		field = record.get(53) ;
		if(NumberUtils.isParsable(field)) { 
			skill.setMP(Integer.parseInt(field));
		}
		

		skill.setName(record.get(62));

		field = record.get(70) ; 
		if(NumberUtils.isParsable(field)) { 
			skill.setDuration(Integer.parseInt(field));
		}
		
		// parse type 
        SkillType type = SkillType.Passive ;
        
        field = record.get(8) ; // {0 , 1 , 2} 0 = passive
        
        if(!field.equals("0")) { 
        	type = SkillType.parseType(skill.getLongId()) ; 
        }
        
        skill.setType(type);

        return skill ; 
	}
	

}
