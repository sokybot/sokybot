/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain.npc;

import java.beans.PropertyChangeSupport;

import lombok.Data;


/**
 *
 * @author AMROO
 */
@Data
public class CharacterEntity {

	
	

	private int refId ; 
	
	private int charId; // shared
	private String charName; // shared
	private byte charScale; // shared
	private byte charLvl; // shared -- Current charLvl
	private long charEXPOffset; // shared
	private short charSTR; // shared
	private short charINT; // shared
	private short charStatPoint; // shared
	private int charHP; // shared
	private int charMP; // shared
	
	


	
	
}
