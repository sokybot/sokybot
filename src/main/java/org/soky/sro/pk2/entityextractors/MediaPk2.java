/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.sro.pk2.entityextractors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.IOUtils;
import org.sokybot.domain.SilkroadData;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.Pk2File;
import org.sokybot.security.Blowfish;
import org.sokybot.security.IBlowfish;

import static org.sokybot.pk2.io.Pk2IO.getInputStream;

/**
 *
 * @author AMROO
 */
public class MediaPk2 implements IMediaPk2 {

	private IPk2File mediaFile;

	public MediaPk2(IPk2File mediaFile) {
		this.mediaFile = mediaFile;
	}

	@Override
	public Map<String, String> getEntityNames() {

		return this.mediaFile.find("(?i)textdataname.txt").stream().map(jmxFile -> {
			try {
				return new BufferedReader(new InputStreamReader(getInputStream(jmxFile), StandardCharsets.UTF_16LE));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).flatMap(BufferedReader::lines)
				.flatMap((fileEntryName) -> this.mediaFile.find("(?i)" + fileEntryName).stream())
				.flatMap((nameJmxFile) -> {
					// return getInputStream(nameJmxFile) ;
					try {
						return CSVFormat.MYSQL.builder().setTrim(true).build()
								.parse(new BufferedReader(
										new InputStreamReader(getInputStream(nameJmxFile), StandardCharsets.UTF_16LE)))
								.stream();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}).filter((record) -> {
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
				}));

	}

	@Override
	public Stream<ItemEntity> getItemEntities() {
		return Pk2Extractors.getExtractorForEntity(ItemEntity.class).extract(this.mediaFile)
				.peek(item -> item.setName("Here We Must Get Item name From EntityNames"));
	}

	@Override
	public SilkroadData getSilkroadData() {

		return null;
		// return Pk2Extractors.getSilkroadDataExtractor(this.mediaFile).extract();
	}

	@Override
	public Set<SkillEntity> getSkillEntities() {

		return null;
		// return Pk2Extractors.getSkillEntitiesExtractor(this.mediaFile).extract() ;
	}

	@Override
	public void close() throws IOException {
		this.mediaFile.close();
	}

	public static IMediaPk2 createInstance(String silkroadPath) {

		return new MediaPk2(new Pk2File(silkroadPath + "\\Media.pk2"));
	}

	@Override
	public int extractVersion() {
		IBlowfish bf = Blowfish.newInstance("SILKROAD".getBytes());

		InputStream inputStream;
		try {
			inputStream = getInputStream(this.mediaFile.findFirst("SV.T").get());
			int verLen = EndianUtils.readSwappedInteger(inputStream);
			byte ver[] = IOUtils.readFully(inputStream, verLen);
			ver = bf.decode(0, ver);

			return Integer.parseInt(new String(ver).trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}

}
