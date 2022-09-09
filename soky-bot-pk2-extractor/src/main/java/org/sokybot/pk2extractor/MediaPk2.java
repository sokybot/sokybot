/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.pk2extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.sokybot.domain.Division;
import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.JMXFile;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;
import org.sokybot.pk2extractor.exception.Pk2MissedResourceException;
import org.sokybot.security.Blowfish;
import org.sokybot.security.IBlowfish;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toString;
import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toByteArray;
import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toBufferedReader;

import static org.sokybot.pk2extractor.Pk2ExtractorUtils.toCSVRecordStream ;




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

	private void checkNames() throws Pk2ExtractionException {
		if (this.names == null) {
			this.names = getEntityNames();
		}
		
	}
/*
 *
 * Client code should handle the case where : 
 * 1 -  Jmx File not exists 
 * 2 -  and if this file is misformatted
 */

	private Map<String, String> getEntityNames() throws Pk2ExtractionException  {

		return this.mediaFile.find("(?i)textdataname.txt").stream()
				.map(jmxFile -> toBufferedReader(jmxFile))
		        .flatMap(BufferedReader::lines)
				.flatMap((fileEntryName) -> this.mediaFile.find("(?i)" + fileEntryName).stream())
				.flatMap((nameJmxFile) -> toCSVRecordStream(nameJmxFile))
				.filter((record) -> {
					if (record.size() < 2 || record.hasComment())
						return false;

					String firstField = record.get(0);

					return !firstField.startsWith("//") && !firstField.equals("0") && !firstField.isBlank();
				}).collect(Collectors.toMap((record) -> record.get(1), (record) -> {

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
	public Stream<ItemEntity> getItemEntities() throws Pk2ExtractionException{
		checkNames();
		return Pk2Extractors.getExtractorForEntity(ItemEntity.class).extract(this.mediaFile)
				.peek((item) -> item.setName(names.get(item.getName())));
	}

	@Override
	public Stream<SkillEntity> getSkillEntities() throws Pk2ExtractionException {
		checkNames();
		return Pk2Extractors.getExtractorForEntity(SkillEntity.class).extract(this.mediaFile)
				.peek((skill) -> skill.setName(names.get(skill.getName())));

	}

	@Override
	public void close() throws IOException {
		this.mediaFile.close();
		this.onCloseAction.run();
	}

	@Override
	public int extractVersion() throws Pk2ExtractionException {

		return Try.of(() -> {
			IBlowfish bf = Blowfish.newInstance("SILKROAD".getBytes());
			JMXFile file = this.mediaFile.findFirst("SV.T").orElseThrow(()->new Pk2MissedResourceException(null, null, null)) ; 
			InputStream inputstream = getInputStream(file);
			int verLen = EndianUtils.readSwappedInteger(inputstream);
			byte ver[] = IOUtils.readFully(inputstream, verLen);
			ver = bf.decode(0, ver);
			log.debug("Decoded version value extracted from mediaPk2 {} ", ver);
			return Integer.parseInt(new String(ver).trim());
		}).recover(NoSuchElementException.class, (ex) -> {
			log.error("Colud not find SV.T file ");
			return -1;
		}).recover(NumberFormatException.class, (ex) -> {

			log.error("Invalid version value at SV.T file ");
			return -1;
		}).get();
	}

//	@Override
//	public int extractVersion2() throws IOException {
//		IBlowfish bf = Blowfish.newInstance("SILKROAD".getBytes());
//
//		InputStream inputStream;
//		try {
//			inputStream = getInputStream(this.mediaFile.findFirst("SV.T").get());
//			int verLen = EndianUtils.readSwappedInteger(inputStream);
//			byte ver[] = IOUtils.readFully(inputStream, verLen);
//			ver = bf.decode(0, ver);
//
//			return Integer.parseInt(new String(ver).trim());
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//
//		return -1;
//	}

	
	@Override
	public int extractPort() throws Pk2ExtractionException {
	
		return Try.of(() -> {
			JMXFile jmx = this.mediaFile.findFirst("(?i)gateport.txt").get();
			String thePort = Pk2ExtractorUtils.toString(jmx).trim();
			return NumberUtils.isParsable(thePort) ? Integer.parseInt(thePort) : -1;
		}).recover(NoSuchElementException.class, (e) -> -1)
		  .get();

		// IOUtils.toString(getInputStream(null)).trim() ;
		// return this.mediaFile.findFirst("(?i)gateport.txt").map((jmx) -> {
		// String thePort = IOUtils.toString(getInputStream(jmx), "UTF-8").trim();

		// if (NumberUtils.isParsable(thePort)) {
		// return Integer.parseInt(thePort);
		// } else {
		// log.error("Extracted Port is not parsable --> {}", thePort);
		// }

		// return -1;
		// }).get();

		// JMXFile portFile =
		// if (portFile == null)
		// return -1;

		// return Integer.parseInt(new String(reader.getFileBytes(portFile)).trim());
	}

	@Override
	public DivisionInfo extractDivisionInfo() throws Pk2ExtractionException {
		
		return this.mediaFile.findFirst("divisioninfo.txt").map((jmxFile) -> {
			try {
				DivisionInfo divInfo = new DivisionInfo();
				
				ByteBuffer buffer = ByteBuffer.wrap(toByteArray(jmxFile)) 
											.order(ByteOrder.LITTLE_ENDIAN);
				
				//ByteBuffer buffer = ByteBuffer.wrap(toByteArray(getInputStream(jmxFile)))
						
				divInfo.local = buffer.get();
				byte divCount = buffer.get();

				Stream.generate(Division::new).limit(divCount).forEach((div) -> {
					byte[] nameBytes = new byte[buffer.getInt()];
					buffer.get(nameBytes);
					div.name = new String(nameBytes);
					buffer.get(); // skip 1 byte

					byte ipCount = buffer.get();

					for (int i = 0; i < ipCount; i++) {
						byte[] ipBytes = new byte[buffer.getInt()];
						buffer.get(ipBytes);
						div.addHost(new String(ipBytes));
						buffer.get(); // skip 1 byte
					}

					divInfo.addDivision(div);

				});

				return divInfo;
			} catch (IOException ex) {

				ex.printStackTrace();
				log.error("Colud not Open input stream for {} at path {} ", jmxFile.getName(), jmxFile.getPkFilePath());
				return null;
			}
		}).orElseThrow(() -> new RuntimeException(""));

		// byte bytes[] = reader.getFileBytes("divisioninfo.txt");
		// BinaryReader binaryReader = new BinaryReader(bytes);
		// divisionInfo.local = binaryReader.getByte().get();
		// byte DivisionCount = binaryReader.getByte().get();
		// for (byte i = 0; i < DivisionCount; i++) {
		// Division division = new Division();
		// int NameLength = binaryReader.getDword().toInteger();
		// String Name = binaryReader.getSTR(NameLength).get();
		// division.name = Name;
		// binaryReader.skip(1);
		// byte ipCount = binaryReader.getByte().get();
		// for (byte x = 0; x < ipCount; x++) {
		// int ipLength = binaryReader.getDword().toInteger();
		// String ip = binaryReader.getSTR(ipLength).get();

		// division.addHost(ip);
		// binaryReader.skip(1);
		// }

		// divisionInfo.addDivision(division);
		// }
		// return divisionInfo;
	}

	@Override
	public SilkroadType extractType() throws Pk2ExtractionException {

		return Try.of(()->this.mediaFile.findFirst("type.txt").get())
				.mapTry((jmx)->Pk2ExtractorUtils.toString(jmx))
				.map((theContent)->{
					final SilkroadType type = new SilkroadType();
					Stream.of(StringUtils.split(theContent, System.getProperty("line.separator")))
					.forEach((line) -> {
						String parts[] = StringUtils.split(line, "=");
						type.addProperty(parts[0].trim(), parts[1].replace("\"", " ").trim());
					});
					return type ;
				}).get() ;
		
//		return this.mediaFile.findFirst("type.txt").map((jmxFile) -> {
//
//			try {
//				String typeStr = new String(toByteArray(getInputStream(jmxFile)));
//
//				final SilkroadType type = new SilkroadType();
//				Stream.of(StringUtils.split(typeStr, System.getProperty("line.separator"))).forEach((line) -> {
//					String parts[] = StringUtils.split(line, "=");
//					type.addProperty(parts[0].trim(), parts[1].replace("\"", " ").trim());
//				});
//
//				return type;
//			} catch (IOException ex) {
//				ex.printStackTrace();
//				return null;
//			}
//		}).get();

		// try {

		// byte[] bytes = reader.getFileBytes("type.txt");

		// String Type = new String(toByteArray(getInputStream(null)));
		// String[] lines = Type.split(System.getProperty("line.separator"));

		// SilkroadType type = new SilkroadType();
		// int x = 0;
		// for (int i = 0; i < 2; i++) {
		// for (; x < lines.length;) {
		// String parts[] = lines[x++].split("=");

		// if (parts[0].trim().equalsIgnoreCase("Language") ||
		// parts[0].trim().equalsIgnoreCase("Country")) {
		// type.addProperty(parts[0].trim(), parts[1].replace("\"", " ").trim());

		// break;
		// }
		// ++x ;
		// }
		// }

		// return type;
		// }catch(IOException ex) {

		// }

	}
}
