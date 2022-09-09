/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain.items;


/**
 *
 * @author AMROO
 */
public abstract  class ItemRent {
    public final static int RENT_TYPE_ONE  = 1 ; 
    public final static int RENT_TYPE_TWO = 2 ; 
    public final static int RENT_TYPE_THREE = 3 ; 
    protected short canDelete ;
    
    // simple factory method for rent items .....
   public static  ItemRent createRent(int rentType) { 
         switch(rentType) { 
       case RENT_TYPE_ONE : return new RentType1() ; 
       case RENT_TYPE_TWO  : return new RentType2() ; 
       case RENT_TYPE_THREE : return new RentType3() ;         
        }
        return null ; 
    }
   
    public void setCanDelete(short value) {
       this.canDelete = value ;
    }
   public short getCanDelete() {
       return canDelete ; 
    }
    
}
