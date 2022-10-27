package org.sokybot.network.packet;

public interface IStreamReader {

	public byte getByte() ; 
	public short getShort() ; 
	public int getInt() ; 
	public long getLong() ; 
	public byte[] getBytes(int len) ; 
	
	public void skip(int len ) ; 
	
}
