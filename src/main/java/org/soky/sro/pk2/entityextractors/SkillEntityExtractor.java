package org.soky.sro.pk2.entityextractors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import org.soky.sro.model.SkillEntity;
import org.soky.sro.pk2.IPk2File;
import org.soky.sro.pk2.JMXFile;
import sokybot.gamemodel.SkillEntity;
import sokybot.gamemodel.SkillType;
import sokybot.pk2.IPk2Reader;
import sokybot.pk2.JMXFile;

public class SkillEntityExtractor implements IPK2EntityExtractor<Set<SkillEntity>> {

	private IPk2File reader;

	public SkillEntityExtractor(IPk2File reader) {
		this.reader = reader;
	}

	private static int c = 0;

	@Override
	public Set<SkillEntity> extract() {

		Set<SkillEntity> res = new HashSet<>();

		List<JMXFile> skillEntityFiles = this.reader.find("skilldata_(\\d+)(enc)?.txt$");

		skillEntityFiles.forEach(file -> {
			System.out.println("File Name : " + file.getName());

			res.addAll(extractAt(file));

		});

		return res;
	}

	private Set<SkillEntity> extractAt(JMXFile file) {
		Set<SkillEntity> res = new HashSet<>();

		ByteArrayInputStream in = new ByteArrayInputStream(this.reader.getFileBytes(file));
		try {

			CSVParser parser = CSVFormat.MYSQL.builder()
					.setIgnoreEmptyLines(true).setTrim(true).setCommentMarker('/').build()
					.parse(new InputStreamReader(in, Charset.forName("utf-16le")));

			Iterator<CSVRecord> records = parser.iterator();
			while (records.hasNext()) {
				CSVRecord record = records.next();

				if (record.size() < 1 || record.size() < 70)
					continue;

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
				res.add(skill) ; 
				
				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

}
