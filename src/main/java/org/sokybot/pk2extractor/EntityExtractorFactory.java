package org.sokybot.pk2extractor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sokybot.pk2.IPk2File;

public class EntityExtractorFactory implements IEntityExtractorFactory{

	private final Map<String, IMediaPk2> mediaCache  = new ConcurrentHashMap<>(); 
	
	
	@Override
	public  IMediaPk2 getMediaPk2(String gamePath) { 
		
		IMediaPk2 mediaPk2 = this.mediaCache.get(gamePath) ; 
		
		if(mediaPk2 == null) { 
			
			mediaPk2 = new MediaPk2(IPk2File.open(gamePath + "\\Media.pk2") ,
										()-> mediaCache.remove(gamePath));
			mediaCache.put(gamePath, mediaPk2) ; 
		}
		
	
		return mediaPk2 ; 
	}
}
