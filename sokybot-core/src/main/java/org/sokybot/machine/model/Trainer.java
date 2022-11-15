/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.machine.model;

import org.sokybot.domain.CharacterEntity;
import org.sokybot.domain.item.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.DataBinder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

	@Autowired
	@Setter(value = AccessLevel.NONE) 
	private Inventory itemInventory ; 
	
	@Autowired
	@Setter(value = AccessLevel.NONE) 
	private Inventory avaterInventory ; 
	

	
	
	
	
	
}
