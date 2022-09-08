package org.sokybot.pk2extractor;

import java.io.Closeable;
import java.io.IOException;

import java.util.stream.Stream;

import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;



public interface IMediaPk2 extends Closeable {

	

	public SilkroadType extractType() throws Pk2ExtractionException;
	public DivisionInfo extractDivisionInfo() throws Pk2ExtractionException ;
	public int extractPort() throws Pk2ExtractionException ;
	public int extractVersion() throws Pk2ExtractionException;
	
	public Stream<SkillEntity> getSkillEntities() throws Pk2ExtractionException;
	public Stream<ItemEntity> getItemEntities() throws Pk2ExtractionException ;
	//public Map<String, String> getEntityNames() ; 
	
	//public SilkroadData getSilkroadData() ;
	
	//public void close() ;
//	public void open() ;
	
}
