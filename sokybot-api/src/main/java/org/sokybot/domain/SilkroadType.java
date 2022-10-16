/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain;

import java.util.Map;

import lombok.ToString;

/**
 *
 * @author AMROO
 */
@ToString
public class SilkroadType {
    private Map<String , String > properties  ;  
     
    public SilkroadType(Map<String, String> props) {
    	 this.properties = props ; 
    }
    
    public String getLanguage() {
    	return this.properties.getOrDefault("Language", "UNKNOWN") ;
      //  return this.properties.get("Language") ; 
    }
    public String getCountry() {
        return this.properties.getOrDefault("Country" , "UNKNOWN"); 
    }
    
    public String getProperty(String propertyName) { 
    	return this.properties.get(propertyName) ; 
    }
    
    
}
