package org.sokybot.pk2extractor;

import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.function.Failable;
import org.apache.commons.lang3.math.NumberUtils;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.SkillType;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;





public class SkillEntityExtractor implements IPK2EntityExtractor<SkillEntity> {



	
	
//	@Override
//	public Stream<SkillEntity> extract(IPk2File reader) {

//		Set<SkillEntity> res = new HashSet<>();

//		List<JMXFile> skillEntityFiles = this.reader.find("skilldata_(\\d+)(enc)?.txt$");

	//	skillEntityFiles.forEach(file -> {
		//	System.out.println("File Name : " + file.getName());

		//	res.addAll(extractAt(file));

		//});

		//return res.stream();
	//}

	@Override
	public  Stream<SkillEntity> extract(IPk2File pk2File) throws Pk2ExtractionException { 
 
		return pk2File.find("skilldata_(\\d+)(enc)?.txt$")
		       .stream()
		     //  .peek((jmx)->System.out.println(jmx.getName()))
		       .flatMap(Failable.asFunction(Pk2ExtractorUtils::toCSVRecordStream))
		       .filter((r)->r.size() > 70)
		       .map(this::toSkillEntity)
		       .distinct();
		       
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
	
	//private Set<SkillEntity> extractAt(JMXFile file) {
		//Set<SkillEntity> res = new HashSet<>();

		//ByteArrayInputStream in = new ByteArrayInputStream(this.reader.getFileBytes(file));
		
		//ByteArrayInputStream in = null ; 
//		try {
 
			//Pk2ExtractorUtils.toCSVRecordStream(file)
	//		CSVParser parser = CSVFormat.MYSQL.builder()
		//			.setIgnoreEmptyLines(true).setTrim(true).setCommentMarker('/').build()
			//		.parse(new InputStreamReader(in, Charset.forName("utf-16le")));

			//Iterator<CSVRecord> records = parser.iterator();
			//while (records.hasNext()) {
				//CSVRecord record = records.next();

				//if (record.size() < 1 || record.size() < 70)
					//continue;

				//SkillEntity skill = new SkillEntity();

				//String field = record.get(1);
				//if (NumberUtils.isParsable(field)) {

					//skill.setRefId(Integer.parseInt(field));
				//}

				//skill.setLongId(record.get(3));


				//field = record.get(13); // castTime
				//if (NumberUtils.isParsable(field)) {
					//skill.setCastTime(Integer.parseInt(field));
				//}

				//field = record.get(14); // cooldown
			///	if (NumberUtils.isParsable(field)) {
				//	skill.setCooldown(Integer.parseInt(field));
			//	}

			//	field = record.get(22);
				//boolean targetRequired = false;
				
				//if (NumberUtils.isParsable(field)) {
					//targetRequired = BooleanUtils.toBoolean(Byte.valueOf(field));
				//}
				//skill.setTargetRequired(targetRequired);
				
			//	field = record.get(53) ;
			//	if(NumberUtils.isParsable(field)) { 
				//	skill.setMP(Integer.parseInt(field));
			//	}
				

				//skill.setName(record.get(62));

			//	field = record.get(70) ; 
			//	if(NumberUtils.isParsable(field)) { 
				//	skill.setDuration(Integer.parseInt(field));
				//}
				
				// parse type 
                //SkillType type = SkillType.Passive ;
                
                //field = record.get(8) ; // {0 , 1 , 2} 0 = passive
                
                //if(!field.equals("0")) { 
                //	type = SkillType.parseType(skill.getLongId()) ; 
                //}
                
                //skill.setType(type);
				//res.add(skill) ; 
				
				
			//}

	//	} catch (IOException e) {
		//	e.printStackTrace();
		//}

	//	return res;
	//}

}
