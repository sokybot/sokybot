package org.sokybot.packet;

public interface IPacketBuilder {

	
	public IPacketBuilder put(byte value) ; 
	public IPacketBuilder putShort(short value) ;
	public IPacketBuilder putInt(int value) ; 
	public IPacketBuilder putLong(long value) ; 
	public IPacketBuilder putBytes(byte[] bytes) ; 
	
	
	public MutablePacket build() ; 
}
