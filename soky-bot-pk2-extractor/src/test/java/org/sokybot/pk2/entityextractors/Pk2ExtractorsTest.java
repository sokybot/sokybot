package org.sokybot.pk2.entityextractors;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.pk2extractor.IPK2EntityExtractor;
import org.sokybot.pk2extractor.Pk2ExtractorUtils;
import org.sokybot.pk2extractor.Pk2Extractors;

class Pk2ExtractorsTest {

	@Test
	void testRetriveItemEntityExtractor() {
		IPK2EntityExtractor<ItemEntity> itemEntityExtractor = 
				Pk2ExtractorUtils.getExtractorForEntity(ItemEntity.class) ; 

		assertNotNull(itemEntityExtractor);
	
	}

	@Test
	void testRetriveSkillEntityExtractor() { 
		IPK2EntityExtractor<SkillEntity> skillEntityExtractor = 
				Pk2ExtractorUtils.getExtractorForEntity(SkillEntity.class) ; 

		assertNotNull(skillEntityExtractor);
		
	}

}
