/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.ToString;

/**
 *
 * @author AMROO
 */
@ToString
public class Division {
   public  String name ; 
   private final  List<String>  hosts   ; 

    public Division() {
        hosts = new ArrayList<>() ; 
    }
    public void addHost(String host) {
        this.hosts.add(host) ; 
    }
    public List<String> getHosts() {
        return this.hosts ; 
    }
    public String getRandomHost() {
        if(this.hosts != null){
            Random rnd = new Random( );

            int pos = rnd.nextInt(this.hosts.size()) ;
         return this.hosts.get(pos) ; 
        }return null ;
    }
    public boolean isExistsHost(String host) { 
    	for(String h : hosts) { 
    		if(h.equalsIgnoreCase(host)) { 
    			return true ; 
    		}
    	}
    	return false ; 
    }
    
}
