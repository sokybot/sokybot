package org.sokybot.pk2.entityextractors;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.pk2extractor.IPK2EntityExtractor;
import org.sokybot.pk2extractor.Pk2Extractors;

class Pk2ExtractorsTest {

	@Test
	void testRetriveExistsExtractors() {
		IPK2EntityExtractor<ItemEntity> itemEntityExtractor = 
				Pk2Extractors.getExtractorForEntity(ItemEntity.class) ; 
		assertNotNull(itemEntityExtractor);
	}

	@Test
	void testRead() { 
		
		String packName = Pk2Extractors.class.getPackage().getName().replaceAll("[.]", "/") ; 
		 System.out.println("PackName : " + packName) ;
		new BufferedReader(
				new InputStreamReader(
						
						ClassLoader
						.getSystemResourceAsStream(packName)))
		.lines().forEach((line)->{
			System.out.println(line) ;
		});;
		 
	}
}
