/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.packet;


/**
 *
 * @author AMROO
 */
public interface IPacketReader {
    
   
	 public byte[] readBytes(int pos ) throws IndexOutOfBoundsException; 
    public byte[] readBytes(int pos  , int length) throws IndexOutOfBoundsException; 
    public byte readByte(int pos )throws IndexOutOfBoundsException ; 
    public short readShort(int pos) throws IndexOutOfBoundsException; 
    public int readInt(int pos)throws IndexOutOfBoundsException ; 
    public long readLong(int pos) throws IndexOutOfBoundsException;
    public int remainingBytes(int offset)throws IndexOutOfBoundsException ; 
    
    public IStreamReader asStreamReader() ; 
    
    
    
}
