package org.soky.sro.security;

public interface ICountSecurity {

	public byte generateCountByte(); 
	
	public void configur(long seed) ; 
}
