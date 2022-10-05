package org.sokybot.app.converters;

import javax.swing.Icon;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResouceIconConverter implements Converter<Resource, Icon>{

	@Override
	public Icon convert(Resource source) {
		
		String fileName = source.getFilename() ; 
		
		if(fileName.endsWith(".png")) { 
			
			
		}
		
		return null;
	}

}
