package org.sokybot.app.service;


import java.util.Set;

import org.sokybot.service.IGameDAO;

public interface IMachineGroupService {

	
	public void createMachineGroup(String name , String gamePath ) ; 
	
	//public void createMachine(String parentGroup , String trainerName) ; 
	
	public void createMachine(String parentGroup , String trainerName , String... options); 
	
	public Set<String> listGroups() ; 
	
	public IGameDAO getGameDAO(String groupName) ; 
	
	
}
