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
public class Pk2Header {
    String Name ; 
    int Version ; 
    byte Encryption ; 
    byte [] Verify ; 
    byte [] Reserved ; 

    public Pk2Header() {
        
    }
    
    
   public  boolean Encryption() {
        return (Encryption == 1) ; 
    }
    
}
