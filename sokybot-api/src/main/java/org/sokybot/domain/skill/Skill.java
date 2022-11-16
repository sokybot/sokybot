package org.sokybot.domain.skill;


import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Data
public class Skill {

	
	@Delegate
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private SkillEntity skillEntity ; 
	
	private byte isEnabled ; 
	
	public Skill(SkillEntity skillEntity) { 
		this.skillEntity = skillEntity ; 
		
	}
	
}
