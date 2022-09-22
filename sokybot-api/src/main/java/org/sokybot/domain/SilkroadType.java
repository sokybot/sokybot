/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain;

import java.util.HashMap;

import lombok.ToString;

/**
 *
 * @author AMROO
 */
@ToString
public class SilkroadType {
    private HashMap<String , String > properties = new HashMap<>() ; 
    
    public void addProperty(String name , String value) {
        this.properties.put(name, value) ; 
    }
    public void setLanguage(String language) {
        this.properties.put("Language", language) ; 
    }
    public void setCountry(String Country) {
        this.properties.put("Country", Country) ; 
    }
    public String getLanguage() {
    	return this.properties.getOrDefault("Language", "UNKNOWN") ;
      //  return this.properties.get("Language") ; 
    }
    public String getCountry() {
        return this.properties.getOrDefault("Country" , "UNKNOWN"); 
    }
}
