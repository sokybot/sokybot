package org.sokybot.domain.skill;

import org.sokybot.domain.SilkrodEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SkillEntity extends SilkrodEntity {

	
	private String longId;
	private SkillType type;
    private String name ; 
    
	private int castTime ;
	private int cooldown ; 
	private int duration ; 
	private int MP ; 
	
	//private ItemType requiredWeapon ;
	
	private boolean targetRequired ; 
	



	
}
