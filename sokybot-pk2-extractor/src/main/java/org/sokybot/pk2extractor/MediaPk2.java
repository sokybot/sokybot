/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.pk2extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.sokybot.domain.Division;
import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.JMXFile;
import org.sokybot.pk2.Pk2IO;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;
import org.sokybot.pk2extractor.exception.Pk2InvalidResourceFormatException;
import org.sokybot.pk2extractor.exception.Pk2MissedResourceException;
import org.sokybot.security.Blowfish;

import lombok.extern.slf4j.Slf4j;

import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toByteArray;
import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toBufferedReader;

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
		
		return this.mediaFile	.find("(?i)textdataname.txt")
								.stream()
								.map(jmxFile -> toBufferedReader(jmxFile))
								.flatMap(BufferedReader::lines)
								.flatMap((fileEntryName) -> this.mediaFile	.find("(?i)" + fileEntryName)
																			.stream())
								.flatMap(Pk2ExtractorUtils::toCSVRecordStream)
								.filter((record) -> {
									if (record.size() < 2 || record.hasComment())
										return false;

									String firstField = record.get(0);

									return !firstField.startsWith("//") && !firstField.equals("0")
											&& !firstField.isBlank();
								})
								.collect(Collectors.toMap((record) -> record.get(1), (record) -> {

									for (int i = 2; i < record.size(); i++) {

										if (Pattern.matches(".*[a-zA-Z]+.*", record.get(i))) {
											return record.get(i);

										}

									}

									return "UNDEFINED";

								}, (name1, name2) -> {

									return name2;
								}));

	}

	@Override
	public Stream<ItemEntity> getItemEntities() {
		checkNames();
		return Pk2ExtractorUtils.getExtractorForEntity(ItemEntity.class)
							.extract(this.mediaFile)
							.peek((item) -> item.setName(names.get(item.getName())));
	}

	@Override
	public Stream<SkillEntity> getSkillEntities() {
		checkNames();
		return Pk2ExtractorUtils.getExtractorForEntity(SkillEntity.class)
							.extract(this.mediaFile)
							.peek((skill) -> skill.setName(names.get(skill.getName())));

	}

	@Override
	public void close() throws IOException {
		this.mediaFile.close();
		this.onCloseAction.run();
	}

	@Override
	public int extractVersion() {

		return this.mediaFile	.findFirst("SV.T")
								.map(JMXFile::getInputStream)
								.map(Pk2ExtractorUtils::nextChunk)
								.map((bytes) -> Blowfish.newInstance("SILKROAD".getBytes())
														.decode(0, bytes))
								.map(String::new)
								.map(String::trim)
								.map(Pk2ExtractorUtils::toInteger)
								.orElseThrow(() -> new Pk2MissedResourceException("Colud not find SV.T file ", "SV.T"));

	}

	@Override
	public int extractPort() {

		return this.mediaFile	.findFirst("(?i)gateport.txt")
								.map(Pk2ExtractorUtils::toText)
								.map(String::trim)
								.map(Pk2ExtractorUtils::toInteger)
								.orElseThrow(() -> new Pk2MissedResourceException("Could not find gateport.txt file ",
										"gateport.txt"));

	}

	private String nextString(ByteBuffer buffer) { 
		
		try {
		byte[] strBuffer = new byte[buffer.getInt()];
		buffer.get(strBuffer) ; 
		return new String(strBuffer) ; 
		}catch(IndexOutOfBoundsException|BufferUnderflowException ex) { 
			throw new Pk2InvalidResourceFormatException("An error occurred while parsing a joymax resource file ", ex);
		}
		
	}
	private byte nextByte(ByteBuffer buffer) { 
		try { 
			return buffer.get() ; 
		}catch(BufferUnderflowException ex) { 
			throw new Pk2InvalidResourceFormatException("An error occurred while parsing a joymax resource file ", ex);
		}
	}
	
	@Override
	public DivisionInfo extractDivisionInfo(){

		return this.mediaFile	.findFirst("divisioninfo.txt")
								.map((jmxFile) -> {
									DivisionInfo divInfo = new DivisionInfo();
									ByteBuffer buffer = ByteBuffer	.wrap(toByteArray(jmxFile))
																	.order(ByteOrder.LITTLE_ENDIAN);
									divInfo.local = nextByte(buffer) ;
									byte divCount = nextByte(buffer) ;

									Stream	.generate(Division::new)
											.limit(divCount)
											.forEach((div) -> {
												div.name = nextString(buffer) ; 
												nextByte(buffer); // skip 1 byte
												byte ipCount = nextByte(buffer);
												for (int i = 0; i < ipCount; i++) {
													div.addHost(nextString(buffer)) ;
													nextByte(buffer); // skip 1 byte
												}
												divInfo.addDivision(div);
											});

									return divInfo;

								})
								.orElseThrow(() -> new Pk2MissedResourceException(
										"Could not find Media.pk2$divisioninfo.txt file  ", "divisioninfo.txt"));

	}


	@Override
	public SilkroadType extractType() {

		return this.mediaFile	.findFirst("type.txt")
								.map(Pk2ExtractorUtils::toText)
								.map(Pk2ExtractorUtils::toLines)
								.map((lines) -> {
									final SilkroadType type = new SilkroadType();
									AtomicBoolean isEmpty = new AtomicBoolean(true);
									lines	.filter(line -> line.contains("="))

											.forEach((line) -> {
												String parts[] = StringUtils.split(line, "=");
												type.addProperty(parts[0].trim(), parts[1]	.replace("\"", " ")
																							.trim());
												isEmpty.set(false);
											});

									if (isEmpty.get()) {
										throw new Pk2InvalidResourceFormatException(
												"Unexpected format for joymax file type.txt", "type.txt");
									}

									return type;
								})
								.orElseThrow(() -> new Pk2MissedResourceException(
										"Could not find joymax file  type.txt", "type.txt"));

	}

}
