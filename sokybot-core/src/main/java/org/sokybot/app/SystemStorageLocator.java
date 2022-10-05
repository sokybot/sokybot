package org.sokybot.app;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.index.IndexOptions;
import org.h2.index.IndexType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemStorageLocator {

	@Autowired
	Nitrite db ; 
	
	/**
	 * store information for each machine group 
	 * these information includes group-name , game-path 
	 * 
	 */
    @Bean
    NitriteCollection machineGroupRegister() { 
    	NitriteCollection machineGroup =  db.getCollection("machine-group-register") ; 
    	 
    	return machineGroup ; 
    }
    
    /**
     * store information for each tracked game 
     * these information includes game-path , game-version(last supported) 
     * 
     * @return 
     */
    @Bean
    NitriteCollection gameVersionRegister() { 
    	NitriteCollection gameVersionRegistry =  db.getCollection("game-version-register") ; 
    	return gameVersionRegistry ; 
    }
}
