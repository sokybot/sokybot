package org.sokybot;

import org.sokybot.service.IGameDAO;

public interface IGroupContext {

	
	IGameDAO getGameDAO() ; 
	
	IMachineContext [] getMachines() ; 
	
	void installMachine(String name ,String...options) ;  
}
