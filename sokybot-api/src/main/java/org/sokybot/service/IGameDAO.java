package org.sokybot.service;

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

	ItemEntity getItem(int refId);

	SkillEntity getSkill(int refId);
	

}
