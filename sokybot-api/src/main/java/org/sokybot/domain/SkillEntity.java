package org.sokybot.domain;

import org.sokybot.domain.items.ItemType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SkillEntity extends SilkrodEntity {

	
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
