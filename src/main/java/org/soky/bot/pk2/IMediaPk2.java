package org.soky.bot.pk2;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sokybot.gamemodel.ItemEntity;
import sokybot.gamemodel.SkillEntity;
import sokybot.silkroadgroups.model.DivisionInfo;
import sokybot.silkroadgroups.model.SilkroadData;
import sokybot.silkroadgroups.model.SilkroadType;

public interface IMediaPk2 {

	
	//public DivisionInfo extractDivisionInfo() ; 
	//public int extractPort() ; 
	//public int extractVersion();
	public Set<SkillEntity> getSkillEntities(); 
	public Set<ItemEntity> getItemEntities() ; 
	public Map<String, String> getEntityNames() ; 
	
	//public SilkroadType extractType() ;
	public SilkroadData getSilkroadData() ;
	
	public void close() ; 
	public void open() ; 
	
}
