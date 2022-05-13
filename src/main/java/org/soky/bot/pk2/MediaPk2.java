/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.bot.pk2;

import java.util.Map;
import java.util.Set;

import org.soky.bot.pk2.entityextractors.Pk2ExtractorsFactory;

import sokybot.gamemodel.ItemEntity;
import sokybot.gamemodel.SkillEntity;
import sokybot.silkroadgroups.model.SilkroadData;

/**
 *
 * @author AMROO
 */
public class MediaPk2 implements IMediaPk2 {

 
	private IPk2File mediaFile ; 

	public MediaPk2(IPk2File mediaFile) {
		 this.mediaFile = mediaFile ; 
	}

	@Override
	public Map<String, String> getEntityNames() {

		return Pk2ExtractorsFactory.getEntityNamesExtractor(this.mediaFile.getReader()).extract();
	}

	@Override
	public Set<ItemEntity> getItemEntities() {

		return Pk2ExtractorsFactory.getItemEntityExtractor(this.mediaFile.getReader()).extract();
	}

	@Override
	public SilkroadData getSilkroadData() {

		return Pk2ExtractorsFactory.getSilkroadDataExtractor(this.mediaFile.getReader()).extract();
	}

	@Override
	public Set<SkillEntity> getSkillEntities() {
		 
		return Pk2ExtractorsFactory.getSkillEntitiesExtractor(this.mediaFile.getReader()).extract() ; 
	}
	@Override
	public void close() {
 
		this.mediaFile.close();  
	}

	@Override
	public void open() {
 
		this.mediaFile.open();
	}

	
	public static IMediaPk2 createInstance(String silkroadPath) {
		
			return new MediaPk2(new Pk2File(silkroadPath + "\\Media.pk2"));
	}


}
