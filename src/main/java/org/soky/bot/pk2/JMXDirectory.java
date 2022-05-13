/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.bot.pk2;

import java.util.List;

/**
 *
 * @author AMROO
 */
public class JMXDirectory {
   public  String Name ; 
   public  long Position ; 
   public  List<JMXFile> jMXFiles  = null ; 
   public   List<JMXDirectory> subFolders = null ; 
    
   public JMXDirectory(Pk2Entry entry) {
       this.Name = entry.Name ; 
       this.Position = entry.Position ; 
      
   }
}
