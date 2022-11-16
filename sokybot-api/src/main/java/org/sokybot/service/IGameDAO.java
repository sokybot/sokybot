package org.sokybot.service;

import java.util.Optional;

import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.item.ItemEntity;

import org.sokybot.domain.skill.SkillEntity;

public interface IGameDAO {


	
	String getGamePath() ; 

	Byte getLocal() ; 
	
	Integer getVersion();

	SilkroadType getGameType() ;
	
	Integer getPort();

	String getRndHost();

	
	DivisionInfo getDivisionInfo();

	Optional<ItemEntity> findItem(int refId);

	Optional<SkillEntity> findSkill(int refId);
	

	

}
