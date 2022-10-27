package org.sokybot.machine;

import java.io.Serializable;


import lombok.Data;

@Data
public class UserConfig implements Serializable {

	
	
	private static final long serialVersionUID = 1L;

	private String targetHost ; 
	
	private BotType botType = BotType.CLIENT ; 
	
	
	private boolean test1 ; 
	private boolean test2 ;  
	private boolean test3 ; 
	
	
	
	public enum BotType { 
		CLIENT , CLIENTLESS 
	}
	
}
