/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

/**
 *
 * @author AMROO
 */
@ToString
public class DivisionInfo {
  
 public  byte local ; 
   private final List<Division>  divisions ; 

    public DivisionInfo() {
        this.divisions = new ArrayList<>() ; 
        local = 0 ; 
    }
    
    public void addDivision(Division division) {
        this.divisions.add(division) ; 
    }
    public List<Division> getDivisions() {
        return this.divisions ; 
    }
    
    public Division getDivision(String divsionName) {
		
		for (Division d : divisions) {
			if (d.name.equals(divsionName))
				return d;
		}
		return null;
	}

    public Division getDivisionForHost(String host) {
		
		for (Division d : divisions) {
			List<String> hosts = d.getHosts();
			for (String dHost : hosts) {
				if (dHost.equalsIgnoreCase(host))
					return d;
			}
		}
		return null;
	}
    public String[] getDivisionNames() {
		
		String res[] = new String[divisions.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = divisions.get(i).name;
		}
		return res;
	}
	
}
