package org.sokybot.app;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.NitriteCollection; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemStorageLocator {

	@Autowired
	Nitrite db ; 
	
	  
    @Bean
    NitriteCollection machineGroup() { 
    	return db.getCollection("machine-group-collection") ; 
    }
    
    @Bean
    NitriteCollection gameVersionRegistry() { 
    	return db.getCollection("game-version-registry") ; 
    }
}
