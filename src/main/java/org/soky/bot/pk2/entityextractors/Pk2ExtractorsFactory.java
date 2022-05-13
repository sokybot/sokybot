package org.soky.bot.pk2.entityextractors;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sokybot.gamemodel.ItemEntity;
import sokybot.gamemodel.SkillEntity;
import sokybot.pk2.IPk2Reader;
import sokybot.silkroadgroups.model.SilkroadData;

public class Pk2ExtractorsFactory {

	public static IPK2EntityExtractor<Set<ItemEntity>> getItemEntityExtractor(IPk2Reader reader) {
		return new ItemEntityExtractor(reader);
	}

	public static IPK2EntityExtractor<Map<String, String>> getEntityNamesExtractor(IPk2Reader reader) {

		return new NamesExtracor(reader);
	}

	public static IPK2EntityExtractor<SilkroadData> getSilkroadDataExtractor(IPk2Reader reader) {

		return new SilkroadDataExtractor(reader);
	}
	
	public static IPK2EntityExtractor<Set<SkillEntity>> getSkillEntitiesExtractor(IPk2Reader reader) { 
		return new SkillEntityExtractor(reader) ; 
	}
}
