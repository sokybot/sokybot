package org.soky.sro.pk2.entityextractors;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.soky.sro.model.ItemEntity;
import org.soky.sro.model.SilkroadData;
import org.soky.sro.model.SkillEntity;


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
