package org.sokybot.app.service;

import java.util.List;

public interface IGameDistributionMaintainer {

	
	public List<String> listAllDistributions()  ; 
	
	public Integer getLastSupportedVersion(String gamePath) ; 
	/*
	 * return false if path is not registered yet
	 *  or is registered but has old version than current version of the game at directory 
	 * path
	 * 
	 * return true only if game at path has the same version as register
	 */
	
	public boolean isConsistentWith(String gamePath ) ; 
	
	
	/*
	 * update game entities related with game at path @Pram gamePath
	 * 
	 */
	public void fit(String gamePath) ; 
	
	
}
