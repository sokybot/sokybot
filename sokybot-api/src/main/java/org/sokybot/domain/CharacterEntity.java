/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 *
 * @author AMROO
 */
public class CharacterEntity<T extends CharacterEntity<?>> {

	
	T self ; 
	
	protected PropertyChangeSupport changes;

	protected int refId ; 
	
	protected int charId; // shared
	protected String charName; // shared
	protected byte charScale; // shared
	protected byte charLvl; // shared -- Current charLvl
	protected long charEXPOffset; // shared
	protected short charSTR; // shared
	protected short charINT; // shared
	protected short charStatPoint; // shared
	protected int charHP; // shared
	protected int charMP; // shared
	
	
	
	protected CharacterEntity(final Class<T> selfClass) {
		self = selfClass.cast(this) ; 
		this.changes = new PropertyChangeSupport(self) ; 
	}

	public int getCharId() {
		return charId;
	}

	public T setCharId(int objId) {
		int oldValue = this.charId ; 
		this.charId = objId ; 
		this.changes.firePropertyChange("charId", oldValue, this.charId);
	 
		return self ; 
	}

	public String getCharName() {
		return charName;
	}

	public T setCharName(String charName) {
		String oldValue = this.charName; 
		this.charName = charName;
		this.changes.firePropertyChange("charName", oldValue, this.charName);
		return self ; 
	}

	public byte getCharScale() {
		return charScale;
	}

	public T setCharScale(byte scale) {
		byte oldValue = this.charScale ;
		this.charScale = scale;
		this.changes.firePropertyChange("charScale" , oldValue , this.charScale);
		return self ; 
	}
	

	public byte getCharLvl() {
		return charLvl;
	}

	public T setCharLvl(byte lvl) {
		byte oldValue = this.charLvl ; 
		this.charLvl = lvl;
		this.changes.firePropertyChange("charLvl" , oldValue , this.charLvl);
	   return self ; 
	}

	public long getCharEXPOffset() {
		return charEXPOffset;
	}

	public T setCharEXPOffset(long exp) {
		Long oldValue = this.charEXPOffset ; 
		this.charEXPOffset = exp;
		this.changes.firePropertyChange("charEXPOffset" , oldValue , Long.valueOf(this.charEXPOffset) );
	  return self ; 
	}

	public short getCharSTR() {
		return charSTR;
	}

	public T setCharSTR(short STR) {
		short oldValue = this.charSTR ; 
		this.charSTR = STR;
		this.changes.firePropertyChange("charSTR" , oldValue , this.charSTR);
	  return self ; 
	}

	public short getCharINT() {
		return charINT;
	}

	public T setCharINT(short INT) {
		short oldValue = this.charINT ; 
		this.charINT = INT;
		this.changes.firePropertyChange("charINT" , oldValue , this.charINT);
	  return self ; 
	}

	public short getCharStatPoint() {
		return charStatPoint;
	}

	public T setStatPoint(short SP) {
		short oldValue = this.charStatPoint ; 
		this.charStatPoint = SP;
		this.changes.firePropertyChange("charStatPoint" , oldValue , this.charStatPoint);
		return self ; 
	}

	public int getRefId() {
		return refId;
	}

	public T setRefId(int refId) {
		int oldValue = this.refId ; 
		this.refId = refId ; 
		this.changes.firePropertyChange("charRefId" , oldValue , this.refId);
		return self ; 
	}

	public int getCharHP() {
		return charHP;
	}

	public T setCharHP(int HP) {
		int oldValue = this.charHP ; 
		this.charHP = HP ; 
		
		this.changes.firePropertyChange("charHP" , oldValue , this.charHP);
	  return self ; 
	}

	public int getCharMP() {
		return charMP;
	}

	public T setCharMP(int MP) {
		int oldValue = this.charMP ; 
		this.charMP = MP ; 
		this.changes.firePropertyChange("charMP" , oldValue , this.charMP);
	 return self ; 
	}

	
	
	public void addPropertyChangeListener(PropertyChangeListener listener) { 
		
		this.changes.addPropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(String propertyName , PropertyChangeListener listener) { 
		
		this.changes.addPropertyChangeListener(propertyName, listener);
	}


	
	
}
