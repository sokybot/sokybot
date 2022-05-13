/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.bot.pk2;

/**
 *
 * @author AMROO
 */
public class JMXFile {
    String name  ; 
    long Position ;
    int Size ; 
    JMXDirectory Parent  = null ; 
    
    public JMXFile(Pk2Entry entry) {
        this.name = entry.Name ; 
        this.Position = entry.Position ; 
        this.Size = entry.Size ; 
    }

	public String getName() {
		return name;
	}

	public long getPosition() {
		return Position;
	}

	public int getSize() {
		return Size;
	}

	public JMXDirectory getParent() {
		return Parent;
	}

	@Override
	public String toString() {
		return "JMXFile [name=" + name + ", Position=" + Position + ", Size=" + Size + ", Parent=" + Parent + "]";
	}
    
}
