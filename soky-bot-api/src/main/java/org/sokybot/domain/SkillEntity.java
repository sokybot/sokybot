package org.sokybot.domain;

import org.sokybot.domain.items.ItemType;

import lombok.Data;

@Data
public class SkillEntity {

	private int refId;
	private String longId;
	private SkillType type;
    private String name ; 
    
	private int castTime ;
	private int cooldown ; 
	private int duration ; 
	private int MP ; 
	
	private ItemType requiredWeapon ;
	
	private boolean targetRequired ; 
	



	
}
