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



	
	

	@Override
	public  Stream<SkillEntity> extract(IPk2File pk2File) throws Pk2ExtractionException { 
 
		return pk2File.find("skilldata_(\\d+)(enc)?.txt$")
		       .stream()
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
	

}
