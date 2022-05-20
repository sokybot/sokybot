package org.soky.sro.pk2.entityextractors;

import java.util.Map;
import java.util.Set;

import org.soky.sro.model.ItemEntity;
import org.soky.sro.model.SkillEntity;
import org.soky.sro.pk2.IPk2File;


public class Pk2ExtractorsFactory {

	public static IPK2EntityExtractor<Set<ItemEntity>> getItemEntityExtractor(IPk2File reader) {
		return new ItemEntityExtractor(reader);
	}

	public static IPK2EntityExtractor<Map<String, String>> getEntityNamesExtractor(IPk2File reader) {

		return new NamesExtracor(reader);
	}

	public static IPK2EntityExtractor<SilkroadData> getSilkroadDataExtractor(IPk2File pk2File) {

		return new SilkroadDataExtractor(pk2File);
	}
	
	public static IPK2EntityExtractor<Set<SkillEntity>> getSkillEntitiesExtractor(IPk2File pk2File) {
		return new SkillEntityExtractor(pk2File) ;
	}

	//public static <T> IPK2EntityExtractor<T> getExtractorForType()
}
