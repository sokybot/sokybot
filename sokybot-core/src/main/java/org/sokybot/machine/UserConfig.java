package org.sokybot.machine;

import java.io.Serializable;


import lombok.Data;

@Data
public class UserConfig implements Serializable {

	
	
	private static final long serialVersionUID = 1L;

	private String targetGateway ;
	
	private String username ; 
	private String password ; 
	private String passcode ; 
	private String targetAgent ; 

	
	private BotType botType = BotType.CLIENT ; 
	
	
	private boolean autoLogin = false ; 
	
	
	private boolean test1 ; 
	private boolean test2 ;  
	private boolean test3 ; 
	
	
	
	public enum BotType { 
		CLIENT , CLIENTLESS 
	}
	


	
}
