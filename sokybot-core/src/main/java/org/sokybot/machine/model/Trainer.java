/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.machine.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.sokybot.domain.item.ItemEntity;
import org.sokybot.domain.npc.CharacterEntity;
import org.sokybot.domain.npc.Inventory;
import org.sokybot.domain.npc.Mastery;
import org.sokybot.domain.skill.Skill;
import org.sokybot.domain.skill.SkillType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.DataBinder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Delegate;


/**
 *
 * @author AMROO
 */
@Data
@ToString(callSuper = true) 
@EqualsAndHashCode(callSuper = true)
@Component
public class Trainer extends CharacterEntity {

	private int serverTime;
	private byte maxlvl;
	private int sexpOffSet;
	private long gold;
	private int gatheredExpPoint;
	private byte autoInverstExp;
	private int skillPoint;
	private byte gwanCount;
	private byte dailyPK;
	private short totalPK;
	private int pkPenaltyPoint;
	private byte zerkCount;
	private byte zerkLvl;
	private byte freePVP;
	
	private byte hasMask ; 
	//private byte masteryFlag; //[0 = done, 1 = Mastery] 

	
	
	@Setter(value = AccessLevel.NONE) 
	@Getter(value = AccessLevel.NONE)
	@Delegate
	private Inventory itemInventory  = new Inventory(); 
	
	
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private Map<Integer, Mastery> masteries = new HashMap<>() ; 
	

	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private Map<Integer, Skill> skills = new HashMap<>() ; 
	
	
	public void addSkill(Skill skill) { 
		Objects.requireNonNull(skill, "Mastry object must not null") ;
		this.skills.put(skill.getRefId()  , skill) ; 
	}
	
	public void addMastery(Mastery mastery) { 
		Objects.requireNonNull(mastery, "Mastry object must not null") ;
		this.masteries.put(mastery.getMasteryID() , mastery) ; 
	}
	
	public Optional<Mastery> findMastry(int id) { 
		return Optional.ofNullable(masteries.get(id)) ; 
	}
	
	
	
}
