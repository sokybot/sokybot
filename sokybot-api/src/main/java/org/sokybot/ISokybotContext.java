package org.sokybot;

import org.sokybot.service.IMainFrameConfigurator;

public interface ISokybotContext {

	
	
	IMainFrameConfigurator getFrameConfigurator() ; 
	
	IGroupContext[] getGroups() ; 
	
	void installGroup(String groupName , String gamePath , String...options) ; 
	
}
