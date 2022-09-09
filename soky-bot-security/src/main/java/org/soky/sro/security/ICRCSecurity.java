package org.soky.sro.security;

public interface ICRCSecurity  {

	 public  byte calculate(byte[] data) ; 
	 
	 public void configur(long seed) ; 
}
