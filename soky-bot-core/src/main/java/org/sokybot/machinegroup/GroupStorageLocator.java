package org.sokybot.machinegroup;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.ObjectRepository;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupStorageLocator {

	@Autowired
	private Nitrite db ; 
	
	@Value("${gamePath}")
	private String gamePath ; 
	
	
	@Bean
	public ObjectRepository<ItemEntity> itemEntityRepository() { 
		return this.db.repository(ItemEntity.class).hasKey(this.gamePath).get() ; 
	}
	
	@Bean
	public ObjectRepository<SkillEntity> skillEntityRepository() { 
	  return this.db.repository(SkillEntity.class ).hasKey(this.gamePath).get();	
	}
	
}
