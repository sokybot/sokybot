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
public class EntryBlock {
 private final  Pk2Entry [] block  ; 

    public EntryBlock(Pk2Entry[] block) {
        this.block = block;
    }
  
   
    
    public Iterator getIterator() {
        return new BlockOfEntries() ; 
    }
    public Pk2Entry getLastEntry() {
        return block[19] ; 
    }
    private class BlockOfEntries implements Iterator {
       int index ; 
       
        @Override
        public boolean hasNext() {
           return (index < block.length) ; 
        }

        @Override
        public Object next() {
           if(hasNext()) { 
               return block[index++]; 
           }
           return null ; 
        }
        
    }
}
