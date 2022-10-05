/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain;

/**
 *
 * @author AMROO
 */
public class SilkroadData  {
	
	
	private DivisionInfo divisionInfo = null;
	private SilkroadType silkroadType = null;
	private int port;
	private int version;

	public SilkroadData() { 
		this.divisionInfo = new DivisionInfo() ; 
	}
	
	public byte getLocal() {
		if (this.divisionInfo != null) {
			return this.divisionInfo.local;
		}
		return 0;
	}

	public int getVersion() {
		return this.version;
	}

	public int port() {
		return this.port;
	}

	public String getLanguage() {
		if (this.silkroadType != null) {
			return this.silkroadType.getLanguage();
		}
		return null;
	}

	public String getCountry() {
		if (this.silkroadType != null) {
			return this.silkroadType.getCountry();
		}
		return null;
	}

	public DivisionInfo getDivisionInfo() { 
		return this.divisionInfo ; 
	}

	public SilkroadType getSilkroadType() {
		return silkroadType;
	}

	public void setSilkroadType(SilkroadType silkroadType) {
		this.silkroadType = silkroadType;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setDivisionInfo(DivisionInfo divisionInfo) {
		this.divisionInfo = divisionInfo;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	




	
}
