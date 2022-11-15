package org.sokybot.machine.service;


public interface IAuthenticationService {

	
	public void login(String userName , String password , String agentName) ; 
	public void authenticate() ; 
	public void logout(); 
	
}
