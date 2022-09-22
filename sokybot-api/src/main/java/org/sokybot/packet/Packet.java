/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;



/**
 *
 * @author AMROO
 */
public  class Packet{
  

	ByteBuffer  buffer ; 
	 
	protected Encoding dataEncoding ; 
	
	protected PacketDirection source  = PacketDirection.BOT; 
	
	
	
	public Packet(ByteBuffer buffer) { 
		this.buffer = buffer ; 
		this.buffer.order(ByteOrder.LITTLE_ENDIAN) ; 
	}
	
	

	public short getPacketSize() { 
	    //System.out.println("This.getPacketSize is : " + Integer.toBinaryString(this.buffer.getShort(0) & 0xffff)) ; 
		
		return (short) (this.buffer.getShort(0) & 0x7fff );
	}
	
	public short getOpcode() { 
		return this.buffer.getShort(2) ; 
	}
	
	public byte getCount() { 
		return this.buffer.get(4) ; 
	}
	
	public byte getCRC() { 
		return this.buffer.get(5);
	}
	
	
	
	public Encoding getPacketEncoding() { 
	  //  System.out.println("This.getPacketEncoding is : " + Integer.toBinaryString(this.buffer.getShort(0) & 0xffff)) ; 
		
	  return ((this.buffer.getShort(0) & 0x8000) == 0x8000) ? Encoding.ENCRYPTED : Encoding.PLAIN ; 
				
	}
	
	public Encoding getDataEncoding() { 
	   return this.dataEncoding ; 
	}
	
	public PacketDirection getPacketSource() { 
		return this.source ; 
	}
	
	
    
}
