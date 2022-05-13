/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.bot.pk2;

import Test.primarydatatypes.Qword;

/**
 *
 * @author AMROO
 */
public class Pk2Entry {
  public byte Type ; // 1 JMXDirectory  , 2 JMXFile , 0 null 
  public String Name ; 
  public Qword AccessTime  , CreateTime , ModifyTime; 
  long Position , NextChain ; 
  int Size ; 
  byte [] Padding ; 
  
}
