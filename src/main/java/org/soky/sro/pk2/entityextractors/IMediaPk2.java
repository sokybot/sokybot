package org.soky.sro.pk2.entityextractors;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.sokybot.domain.SilkroadData;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;



public interface IMediaPk2 extends Closeable {

	
	//public DivisionInfo extractDivisionInfo() ;
	//public int extractPort() ;
	public int extractVersion();
	
	public Set<SkillEntity> getSkillEntities();
	public Stream<ItemEntity> getItemEntities() ;
	public Map<String, String> getEntityNames() ; 
	
	//public SilkroadType extractType() ;
	public SilkroadData getSilkroadData() ;
	
	//public void close() ;
//	public void open() ;
	
}
